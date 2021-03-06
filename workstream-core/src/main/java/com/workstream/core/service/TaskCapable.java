package com.workstream.core.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.history.HistoricFormProperty;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Event;
import org.activiti.engine.task.IdentityLinkType;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.CoreConstants;
import com.workstream.core.exception.AttempBadStateException;
import com.workstream.core.exception.AuthenticationNotSetException;
import com.workstream.core.exception.BadArgumentException;
import com.workstream.core.exception.BeanPropertyException;
import com.workstream.core.exception.ResourceNotFoundException;
import com.workstream.core.service.cmd.WsAddCommentCmd;

public class TaskCapable {

	protected final Logger log = LoggerFactory.getLogger(TaskCapable.class);

	public enum UserTaskRole {
		CREATOR, ASSIGNEE, CANDIDATE
	}

	@Autowired
	protected TaskService taskSer;
	@Autowired
	protected TaskEventHelper eventHelper;

	@Autowired
	protected HistoryService hiSer;

	@Autowired
	protected FormService formService;
	@Autowired
	protected ManagementService mgmtService;

	/**
	 * A generic querying method by userId
	 * 
	 * @param role
	 * @param userId
	 * @return
	 */
	public List<Task> filterTaskByUser(UserTaskRole role, String userId,
			int first, int max) {
		TaskQuery q = prepareTaskQueryByUser(role, userId);
		return q.listPage(first, max);
	}

	public long countTaskByUser(UserTaskRole role, String userId) {
		TaskQuery q = prepareTaskQueryByUser(role, userId);
		return q.count();
	}

	/**
	 * A generic querying method by userId
	 * 
	 * @param role
	 * @param userId
	 * @return
	 */
	public List<HistoricTaskInstance> filterArchTaskByUser(UserTaskRole role,
			String userId, int first, int max) {
		HistoricTaskInstanceQuery q = prepareArchTaskQueryByUser(role, userId);
		return q.listPage(first, max);
	}

	public long countArchTaskByUser(UserTaskRole role, String userId) {
		HistoricTaskInstanceQuery q = prepareArchTaskQueryByUser(role, userId);
		return q.count();
	}

	protected HistoricTaskInstanceQuery prepareArchTaskQueryByUser(
			UserTaskRole role, String userId) {
		HistoricTaskInstanceQuery q = hiSer.createHistoricTaskInstanceQuery();
		q.finished().orderByHistoricTaskInstanceEndTime().desc();

		switch (role) {
		case ASSIGNEE:
			q.taskAssignee(userId);
			break;
		case CREATOR:
			q.taskOwner(userId);
			break;
		case CANDIDATE:
			q.taskCandidateUser(userId);
			break;
		default:
			throw new BadArgumentException("Unsupported user task role.");
		}

		return q;
	}

	protected TaskQuery prepareTaskQueryByUser(UserTaskRole role, String userId) {
		TaskQuery q = taskSer.createTaskQuery();

		switch (role) {
		case ASSIGNEE:
			q.taskAssignee(userId);
			break;
		case CREATOR:
			q.taskOwner(userId);
			break;
		case CANDIDATE:
			q.taskCandidateUser(userId).taskUnassigned();
			break;
		default:
			throw new BadArgumentException("Unsupported user task role.");
		}
		return q.orderByTaskCreateTime().desc();
	}

	/**
	 * Doesn't care about org or project. (Process related tasks will also be
	 * included)
	 * 
	 * @param assigneeId
	 * @return
	 */
	public List<Task> filterTaskByAssignee(String assigneeId, int first, int max) {
		return filterTaskByUser(UserTaskRole.ASSIGNEE, assigneeId, first, max);
	}

	/**
	 * Doesn't care about org or project. (Process related tasks will also be
	 * included)
	 * 
	 * @param creatorId
	 * @return
	 */
	public List<Task> filterTaskByCreator(String creatorId, int first, int max) {
		return filterTaskByUser(UserTaskRole.CREATOR, creatorId, first, max);
	}

	public List<Task> filterTaskByCandidateUser(String userId, int first,
			int max) {
		return filterTaskByUser(UserTaskRole.CANDIDATE, userId, first, max);
	}

	protected TaskQuery prepareTaskQueryByCandidateGroup(String... groupIds) {
		TaskQuery q = taskSer.createTaskQuery()
				.taskCandidateGroupIn(Arrays.asList(groupIds)).taskUnassigned()
				.orderByTaskCreateTime().desc();
		return q;
	}

	public List<Task> filterTaskByCandidateGroup(String... groupIds) {
		TaskQuery q = prepareTaskQueryByCandidateGroup(groupIds);
		return q.list();
	}

	public long countTaskByCandidateGroup(String... groupIds) {
		TaskQuery q = prepareTaskQueryByCandidateGroup(groupIds);
		return q.count();
	}

	public Task getTask(String taskId) {
		return taskSer.createTaskQuery().taskId(taskId).singleResult();
	}

	protected Task createTask(String creator, String name, String description,
			Date dueDate, String assigneeId, Integer priority, String parentId,
			Long orgId, Long projectId) {
		Task task = taskSer.newTask();
		if (orgId != null) {
			task.setTenantId(String.valueOf(orgId));
		}
		task.setOwner(creator);
		task.setName(name);
		if (projectId != null) {
			task.setCategory(String.valueOf(projectId));
		}
		if (description != null) {
			task.setDescription(description);
		}
		if (dueDate != null) {
			task.setDueDate(dueDate);
		}
		if (priority != null) {
			task.setPriority(priority);
		}
		if (parentId != null) {
			task.setParentTaskId(parentId);
		}
		taskSer.saveTask(task);

		// for owner and assignee, must invoke addUserIdentityLink
		// because AddIdentityLinkCmd adds event comment to the comments table
		taskSer.addUserIdentityLink(task.getId(), creator,
				IdentityLinkType.OWNER);
		if (assigneeId != null) {
			// task.setAssignee(assigneeId);
			taskSer.addUserIdentityLink(task.getId(), assigneeId,
					IdentityLinkType.ASSIGNEE);
			task.setAssignee(assigneeId);
		}
		return task;
	}

	public Task createSubTask(String creator, Task parentTask, String name,
			String description, Date dueDate, String assigneeId,
			Integer priority) {
		Long orgId = null;
		if (parentTask.getTenantId() != null) {
			orgId = Long.valueOf(parentTask.getTenantId());
		}
		Long projectId = null;
		if (parentTask.getCategory() != null) {
			projectId = Long.valueOf(parentTask.getCategory());
		}

		Task task = createTask(creator, name, description, dueDate, assigneeId,
				priority, parentTask.getId(), orgId, projectId);
		return task;
	}

	public List<Task> getSubTasks(String parentTaskId) {
		List<Task> tasks = taskSer.getSubTasks(parentTaskId);
		Collections.sort(tasks, new Comparator<Task>() {
			@Override
			public int compare(Task o1, Task o2) {
				if (o1.getCreateTime() != null && o2.getCreateTime() != null) {
					return o2.getCreateTime().compareTo(o1.getCreateTime());
				} else {
					return 0;
				}
			}
		});

		return tasks;
	}

	public long countSubTasks(String parentTaskId) {
		// The TaskQuery doesn't provide the ability to query by parentTaskId.
		// So have to query the HistorictTaskInstance.
		HistoricTaskInstanceQuery q = hiSer.createHistoricTaskInstanceQuery();
		q.taskParentTaskId(parentTaskId);
		return q.count();
	}

	public Task updateTask(Task task, Map<String, ? extends Object> props) {
		String id = task.getId();
		String oldAssignee = task.getAssignee();
		String oldOwner = task.getOwner();

		boolean setAssignee = props.containsKey("assignee");
		String newAssignee = (String) props.remove("assignee");
		// must not trigger task.setAssignee, because it will
		// dispatch the TASK_ASSIGNED event
		try {
			BeanUtils.copyProperties(task, props);
			// BeanUtils.populate() got exception No value specified for 'Date'
			// when dueDate == null
			// http://www.blogjava.net/javagrass/archive/2011/10/10/352856.html
			// BeanUtils.populate(task, props);
		} catch (Exception e) {
			log.error("Failed to populate the props to task: {}", props, e);
			throw new BeanPropertyException(e);
		}
		taskSer.saveTask(task);

		if (setAssignee) {
			String assignee = newAssignee;
			if (assignee != null) {
				taskSer.addUserIdentityLink(id, assignee,
						IdentityLinkType.ASSIGNEE);
			} else if (oldAssignee != null) {
				// have to delete the link
				taskSer.deleteUserIdentityLink(id, oldAssignee,
						IdentityLinkType.ASSIGNEE);
			}

		}

		// code blow will create comments for the task
		// see org.activiti.engine.task.Event
		if (props.containsKey("owner")) {
			String owner = (String) props.get("owner");
			if (owner != null) {
				taskSer.addUserIdentityLink(id, owner, IdentityLinkType.OWNER);
			} else if (oldOwner != null) {
				// have to delete the link
				taskSer.deleteUserIdentityLink(id, oldOwner,
						IdentityLinkType.OWNER);
			}
		}

		// create the event if needed
		eventHelper.createEventCommentIfNeeded(id, props);
		return task;
	}

	public Task updateTask(String id, Map<String, ? extends Object> props) {
		Task task = getTask(id);
		if (task == null) {
			log.warn("Trying to update non-existing task id={} with {} ", id,
					props);
			throw new ResourceNotFoundException("No such task");
		}
		return updateTask(task, props);
	}

	/**
	 * Deletes the given task, not deleting historic information that is related
	 * to this task(the task is still in the archive table). If the task belongs
	 * to a running process instance, then an ActivitiException will be thrown:<br/>
	 * The task cannot be deleted because is part of a running process.
	 * 
	 * @param taskId
	 */
	public void deleteTask(String taskId) throws ResourceNotFoundException {
		Task task = getTask(taskId);
		if (task == null) {
			throw new ResourceNotFoundException("No such task");
		}
		deleteTask(task);
	}

	/**
	 * Deletes the given task, not deleting historic information that is related
	 * to this task(the task is still in the archive table). If the task belongs
	 * to a running process instance, then an ActivitiException will be thrown:<br/>
	 * The task cannot be deleted because is part of a running process.
	 * 
	 * @param task
	 */
	@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
	public void deleteTask(Task task) {
		try {
			taskSer.deleteTask(task.getId());
			log.info("Deleted task {}", task);
		} catch (ActivitiObjectNotFoundException e) {
			throw new ResourceNotFoundException("No such task", e);
		} catch (ActivitiException ae) {
			if (ae.getMessage() != null
					&& ae.getMessage().endsWith("part of a running process")) {
				throw new AttempBadStateException(ae.getMessage());
			} else {
				throw ae;
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
	public void completeTask(String taskId) {
		try {
			taskSer.complete(taskId);
		} catch (ActivitiObjectNotFoundException e) {
			throw new ResourceNotFoundException("No such task", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
	public void completeTask(String taskId, Map<String, Object> variables) {
		try {
			taskSer.complete(taskId, variables);
		} catch (ActivitiObjectNotFoundException e) {
			throw new ResourceNotFoundException("No such task", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
	public void claimTask(String taskId, String userId) {
		try {
			taskSer.claim(taskId, userId);
		} catch (ActivitiObjectNotFoundException e) {
			throw new ResourceNotFoundException("No such task", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
	public Comment addTaskComment(String taskId, String message) {
		if (Authentication.getAuthenticatedUserId() == null) {
			throw new AuthenticationNotSetException(
					"No authenticated user, no comments can be made.");
		}

		try {
			// Activiti implementation doesn't support commenting archived
			// entities
			// Comment com = taskSer.addComment(taskId, null, message);
			// so here we use our own implementation
			Comment com = mgmtService.executeCommand(new WsAddCommentCmd(
					taskId, null, message));
			return com;
		} catch (ActivitiObjectNotFoundException e) {
			throw new ResourceNotFoundException("No such task.", e);
		}
	}

	public List<Comment> filterTaskComment(String taskId) {
		return taskSer.getTaskComments(taskId);
	}

	public Comment getTaskComment(String commentId) {
		return taskSer.getComment(commentId);
	}

	public Event getTaskEvent(String eventId) {
		return taskSer.getEvent(eventId);
	}

	public List<Event> filterTaskEvent(String taskId) {
		List<Event> result = taskSer.getTaskEvents(taskId);
		// the order is not correct when a task has both comments and events, so
		// resort is needed
		Collections.sort(result, new Comparator<Event>() {
			@Override
			public int compare(Event event1, Event event2) {
				// reorder the list by time desc
				return event2.getTime().compareTo(event1.getTime());
			}
		});

		return result;
	}

	/**
	 * Filter <b>finished</b> tasks by assigneeId
	 * 
	 * @param assigneeId
	 * @return
	 */
	public List<HistoricTaskInstance> filterArchTaskByAssignee(
			String assigneeId, int first, int max) {
		return filterArchTaskByUser(UserTaskRole.ASSIGNEE, assigneeId, first,
				max);
	}

	/**
	 * Filter <b>finished</b> tasks by creatorId
	 * 
	 * @param assigneeId
	 * @return
	 */
	public List<HistoricTaskInstance> filterArchTaskByCreator(String creator,
			int first, int max) {
		return filterArchTaskByUser(UserTaskRole.CREATOR, creator, first, max);
	}

	public List<HistoricTaskInstance> filterArchTaskByCandidateUser(
			String candidate, int first, int max) {
		return filterArchTaskByUser(UserTaskRole.CANDIDATE, candidate, first,
				max);
	}

	public List<HistoricFormProperty> filterArchTaskFormProperties(String taskId) {
		List<HistoricDetail> details = hiSer.createHistoricDetailQuery()
				.formProperties().taskId(taskId).list();
		List<HistoricFormProperty> formProperties = new ArrayList<HistoricFormProperty>(
				details.size());
		for (HistoricDetail d : details) {
			formProperties.add((HistoricFormProperty) d);
		}
		return formProperties;
	}

	protected HistoricTaskInstanceQuery prepareArchSubTaskQuery(
			String parentTaskId) {
		HistoricTaskInstanceQuery q = hiSer.createHistoricTaskInstanceQuery();
		q.taskParentTaskId(parentTaskId).finished()
				.orderByHistoricTaskInstanceEndTime().desc();
		return q;
	}

	/**
	 * @param parentTaskId
	 * @return
	 */
	public List<HistoricTaskInstance> filterArchSubTasks(String parentTaskId,
			int first, int max) {
		HistoricTaskInstanceQuery q = prepareArchSubTaskQuery(parentTaskId);
		return q.listPage(first, max);
	}

	public long countArchSubTasks(String parentTaskId) {
		HistoricTaskInstanceQuery q = prepareArchSubTaskQuery(parentTaskId);
		return q.count();
	}

	public FormService getFormService() {
		return formService;
	}

	public Map<String, Object> getTaskLocalVariables(String taskId) {
		try {
			Map<String, Object> vars = taskSer.getVariablesLocal(taskId);
			return vars;
		} catch (ActivitiObjectNotFoundException e) {
			throw new ResourceNotFoundException("No such task.", e);
		}
	}

	public void setTaskLocalVariable(String taskId, String key, Object value) {
		taskSer.setVariableLocal(taskId, key, value);
	}

	public void setTaskLocalVariables(String taskId, Map<String, Object> vars) {
		taskSer.setVariablesLocal(taskId, vars);
	}

	public HistoricTaskInstance getArchTask(String taskId) {
		HistoricTaskInstanceQuery q = hiSer.createHistoricTaskInstanceQuery();
		// return q.taskId(taskId).finished().singleResult();
		return q.taskId(taskId).singleResult();
	}

	public HistoricTaskInstance getArchTaskWithVars(String taskId) {
		HistoricTaskInstanceQuery q = hiSer.createHistoricTaskInstanceQuery();
		// return q.taskId(taskId).finished().includeTaskLocalVariables()
		// .singleResult();
		return q.taskId(taskId).includeTaskLocalVariables().singleResult();
	}
}
