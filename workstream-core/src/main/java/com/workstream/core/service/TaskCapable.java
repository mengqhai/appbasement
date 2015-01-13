package com.workstream.core.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
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
import com.workstream.core.exception.AuthenticationNotSetException;
import com.workstream.core.exception.BeanPropertyException;
import com.workstream.core.exception.ResourceNotFoundException;

public class TaskCapable {

	protected final Logger log = LoggerFactory.getLogger(TaskCapable.class);

	@Autowired
	protected TaskService taskSer;
	@Autowired
	protected TaskEventHelper eventHelper;

	@Autowired
	protected HistoryService hiSer;

	@Autowired
	protected FormService formService;

	/**
	 * Doesn't care about org or project. (Process related tasks will also be
	 * included)
	 * 
	 * @param assigneeId
	 * @return
	 */
	public List<Task> filterTaskByAssignee(String assigneeId) {
		TaskQuery q = taskSer.createTaskQuery().taskAssignee(assigneeId)
				.orderByTaskCreateTime().desc();
		return q.list();
	}

	/**
	 * Doesn't care about org or project. (Process related tasks will also be
	 * included)
	 * 
	 * @param creatorId
	 * @return
	 */
	public List<Task> filterTaskByCreator(String creatorId) {
		TaskQuery q = taskSer.createTaskQuery().taskOwner(creatorId).orderByTaskCreateTime().desc();
		return q.list();
	}

	public List<Task> filterTaskByCandidateGroup(String... groupIds) {
		TaskQuery q = taskSer.createTaskQuery()
				.taskCandidateGroupIn(Arrays.asList(groupIds)).taskUnassigned().orderByTaskCreateTime().desc();
		return q.list();
	}

	public Task getTask(String taskId) {
		return taskSer.createTaskQuery().taskId(taskId).singleResult();
	}

	public List<Task> getSubTasks(String parentTaskId) {
		return taskSer.getSubTasks(parentTaskId);
	}

	public Task updateTask(Task task, Map<String, ? extends Object> props) {
		String id = task.getId();
		String oldAssignee = task.getAssignee();
		String oldOwner = task.getOwner();
		try {
			BeanUtils.populate(task, props);
		} catch (Exception e) {
			log.error("Failed to populate the props to task: {}", props, e);
			throw new BeanPropertyException(e);
		}
		taskSer.saveTask(task);

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
		if (props.containsKey("assignee")) {
			String assignee = (String) props.get("assignee");
			if (assignee != null) {
				taskSer.addUserIdentityLink(id, assignee,
						IdentityLinkType.ASSIGNEE);
			} else if (oldAssignee != null) {
				// have to delete the link
				taskSer.deleteUserIdentityLink(id, oldAssignee,
						IdentityLinkType.ASSIGNEE);
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

		Comment com = taskSer.addComment(taskId, null, message);
		return com;
	}

	public List<Comment> filterTaskComment(String taskId) {
		return taskSer.getTaskComments(taskId);
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
	public List<HistoricTaskInstance> filterArchTaskByAssignee(String assigneeId) {
		return hiSer.createHistoricTaskInstanceQuery().taskAssignee(assigneeId)
				.finished().list();
	}

	/**
	 * Filter <b>finished</b> tasks by creatorId
	 * 
	 * @param assigneeId
	 * @return
	 */
	public List<HistoricTaskInstance> filterArchTaskByCreator(String creator) {
		return hiSer.createHistoricTaskInstanceQuery().taskOwner(creator)
				.finished().list();
	}

	public FormService getFormService() {
		return formService;
	}

}
