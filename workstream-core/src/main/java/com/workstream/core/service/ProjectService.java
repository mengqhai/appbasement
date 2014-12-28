package com.workstream.core.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.activiti.engine.TaskService;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.CoreConstants;
import com.workstream.core.model.Organization;
import com.workstream.core.model.Project;
import com.workstream.core.persistence.IOrganizationDAO;
import com.workstream.core.persistence.IProjectDAO;

@Service
@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
public class ProjectService {

	private final Logger log = LoggerFactory.getLogger(ProjectService.class);

	@Autowired
	private IOrganizationDAO orgDao;

	@Autowired
	private TaskService taskSer;

	@Autowired
	private IProjectDAO proDao;

	@Autowired
	private TaskEventHelper eventHelper;

	public Project createProject(Organization org, String name) {
		return createProject(org, name, null, null, null);
	}

	public Project createProject(Organization org, String name, Date startTime,
			Date dueTime, String description) {
		org = orgDao.reattachIfNeeded(org, org.getId());
		Project pro = new Project();
		if (startTime != null) {
			pro.setStartTime(startTime);
		}
		if (dueTime != null) {
			pro.setDueTime(dueTime);
		}
		if (description != null) {
			pro.setDescription(description);
		}
		pro.setName(name);
		pro.setOrg(org);
		proDao.persist(pro);
		log.debug("Created project {}.", pro);
		return pro;
	}

	public Project getProject(Long id) {
		return proDao.findById(id);
	}

	public void updateProject(Long id, Map<String, ? extends Object> props) {
		Project pro = proDao.findById(id);
		if (pro == null) {
			log.warn("Trying to update non-existing project id={} with {} ",
					id, props);
		}
		try {
			BeanUtils.populate(pro, props);
		} catch (Exception e) {
			log.error("Failed to populate the props to project: {}", props, e);
			throw new RuntimeException(e);
		}
	}

	public Collection<Project> filterProject(Organization org) {
		return proDao.filterFor(org);
	}

	public Task createTask(Long projectId, String creator, String name) {
		return createTask(projectId, creator, name, null, null, null, null);
	}

	public Task createTask(Long projectId, String creator, String name,
			String description, Date dueDate, String assigneeId,
			Integer priority) {
		Project pro = proDao.findById(projectId);
		return createTask(pro, creator, name, description, dueDate, assigneeId,
				priority);
	}

	public Task createTask(Project pro, String creator, String name,
			String description, Date dueDate, String assigneeId,
			Integer priority) {
		pro = proDao.reattachIfNeeded(pro, pro.getId());
		Task task = taskSer.newTask();
		task.setTenantId(String.valueOf(pro.getOrg().getId()));
		task.setOwner(creator);
		task.setName(name);
		task.setCategory(String.valueOf(pro.getId()));
		if (description != null) {
			task.setDescription(description);
		}
		if (dueDate != null) {
			task.setDueDate(dueDate);
		}
		if (priority != null) {
			task.setPriority(priority);
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
		}
		return task;
	}

	public List<Task> filterTask(Project pro) {
		pro = proDao.reattachIfNeeded(pro, pro.getId());
		if (pro == null) {
			log.warn("Filtering tasks for a non-existing project {} ", pro);
			return new ArrayList<Task>();
		}

		Long orgId = pro.getOrg().getId();
		Long proId = pro.getId();

		TaskQuery q = taskSer.createTaskQuery()
				.taskTenantId(String.valueOf(orgId))
				.taskCategory(String.valueOf(proId));
		return q.list();
	}

	public List<Task> filterTask(Project pro, String assignee) {
		pro = proDao.reattachIfNeeded(pro, pro.getId());
		if (pro == null) {
			log.warn("Filtering tasks for a non-existing project {} ", pro);
			return new ArrayList<Task>();
		}

		Long orgId = pro.getOrg().getId();
		Long proId = pro.getId();

		TaskQuery q = taskSer.createTaskQuery()
				.taskTenantId(String.valueOf(orgId))
				.taskCategory(String.valueOf(proId)).taskAssignee(assignee);
		return q.list();
	}

	/**
	 * Doesn't care about org or project.
	 * 
	 * @param assigneeId
	 * @return
	 */
	public List<Task> fitlerTaskByAssignee(String assigneeId) {
		TaskQuery q = taskSer.createTaskQuery().taskAssignee(assigneeId);
		return q.list();
	}

	/**
	 * Doesn't care about org or project.
	 * 
	 * @param creatorId
	 * @return
	 */
	public List<Task> filterTaskByCreator(String creatorId) {
		TaskQuery q = taskSer.createTaskQuery().taskOwner(creatorId);
		return q.list();
	}

	public Task getTask(String taskId) {
		return taskSer.createTaskQuery().taskId(taskId).singleResult();
	}

	public Task updateTask(String id, Map<String, ? extends Object> props) {
		Task task = taskSer.createTaskQuery().taskId(id).singleResult();
		if (task == null) {
			log.warn("Trying to update non-existing task id={} with {} ", id,
					props);
		}
		try {
			BeanUtils.populate(task, props);
		} catch (Exception e) {
			log.error("Failed to populate the props to task: {}", props, e);
			throw new RuntimeException(e);
		}
		taskSer.saveTask(task);
		// see org.activiti.engine.task.Event
		if (props.containsKey("owner")) {
			String owner = (String) props.get("owner");
			taskSer.addUserIdentityLink(id, owner, IdentityLinkType.OWNER);
		}
		if (props.containsKey("assignee")) {
			String owner = (String) props.get("assignee");
			taskSer.addUserIdentityLink(id, owner, IdentityLinkType.ASSIGNEE);
		}

		// create the event if needed
		eventHelper.createEventCommentIfNeeded(id, props);
		return task;
	}

	/**
	 * Deletes the given task, not deleting historic information that is related
	 * to this task(the task is still in the archive table).
	 * 
	 * @param task
	 */
	public void deleteTask(Task task) {
		taskSer.deleteTask(task.getId());
		log.info("Deleted task {}", task);
	}

	public void deleteProject(Project pro) {
		List<Task> tasks = filterTask(pro);
		for (Task task : tasks) {
			deleteTask(task);
		}
		pro = proDao.reattachIfNeeded(pro, pro.getId());
		proDao.remove(pro);
		log.info("Deleted project {}", pro);
	}

	public void completeTask(String taskId) {
		taskSer.complete(taskId);
	}

	public Comment addTaskComment(String taskId, String message) {
		if (Authentication.getAuthenticatedUserId() == null) {
			throw new RuntimeException(
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

}