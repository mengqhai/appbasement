package com.workstream.core.service;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ManagementService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.history.NativeHistoricTaskInstanceQuery;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.activiti.engine.task.TaskInfoQuery;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.CoreConstants;
import com.workstream.core.exception.AttempBadStateException;
import com.workstream.core.exception.BeanPropertyException;
import com.workstream.core.exception.ResourceNotFoundException;
import com.workstream.core.model.Organization;
import com.workstream.core.model.Project;
import com.workstream.core.persistence.IOrganizationDAO;
import com.workstream.core.persistence.IProjectDAO;
import com.workstream.core.service.cmd.CreateRecoveryTaskCmd;
import com.workstream.core.service.cmd.DeleteHistoricTaskNoCascadeCmd;

@Service
@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
public class ProjectService extends TaskCapable {

	protected final Logger log = LoggerFactory.getLogger(ProjectService.class);

	@Autowired
	private IOrganizationDAO orgDao;

	@Autowired
	private IProjectDAO proDao;

	@Autowired
	protected ManagementService mgmtService;

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

	public void updateProject(Long id, Map<String, ? extends Object> props)
			throws BeanPropertyException {
		Project pro = proDao.findById(id);
		if (pro == null) {
			log.warn("Trying to update non-existing project id={} with {} ",
					id, props);
		}
		try {
			BeanUtils.populate(pro, props);
		} catch (Exception e) {
			log.error("Failed to populate the props to project: {}", props, e);
			throw new BeanPropertyException(e);
		}
	}

	public Collection<Project> filterProject(Organization org, int first,
			int max) {
		return proDao.filterFor(org, first, max);
	}

	public Long countProject(Organization org) {
		return proDao.countFor(org);
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
			Integer priority, String parentId) {
		pro = proDao.reattachIfNeeded(pro, pro.getId());
		// Task task = taskSer.newTask();
		// task.setTenantId(String.valueOf(pro.getOrg().getId()));
		// task.setOwner(creator);
		// task.setName(name);
		// task.setCategory(String.valueOf(pro.getId()));
		// if (description != null) {
		// task.setDescription(description);
		// }
		// if (dueDate != null) {
		// task.setDueDate(dueDate);
		// }
		// if (priority != null) {
		// task.setPriority(priority);
		// }
		// if (parentId != null) {
		// task.setParentTaskId(parentId);
		// }
		// taskSer.saveTask(task);
		//
		// // for owner and assignee, must invoke addUserIdentityLink
		// // because AddIdentityLinkCmd adds event comment to the comments
		// table
		// taskSer.addUserIdentityLink(task.getId(), creator,
		// IdentityLinkType.OWNER);
		// if (assigneeId != null) {
		// // task.setAssignee(assigneeId);
		// taskSer.addUserIdentityLink(task.getId(), assigneeId,
		// IdentityLinkType.ASSIGNEE);
		// }
		// return task;
		return createTask(creator, name, description, dueDate, assigneeId,
				priority, parentId, pro.getOrg().getId(), pro.getId());
	}

	public Task createTask(Project pro, String creator, String name,
			String description, Date dueDate, String assigneeId,
			Integer priority) {
		return createTask(pro, creator, name, description, dueDate, assigneeId,
				priority, null);
	}

	protected TaskInfoQuery<? extends TaskInfoQuery<?, ?>, ? extends TaskInfo> prepairTaskInfoQuery(
			Project pro,
			TaskInfoQuery<? extends TaskInfoQuery<?, ?>, ? extends TaskInfo> q) {
		pro = proDao.reattachIfNeeded(pro, pro.getId());
		if (pro == null) {
			log.warn("Filtering tasks for a non-existing project {} ", pro);
			return null;
		}

		Long orgId = pro.getOrg().getId();
		Long proId = pro.getId();
		q.taskTenantId(String.valueOf(orgId)).taskCategory(
				String.valueOf(proId)).orderByTaskCreateTime().desc();
		return q;
	}

	protected TaskInfoQuery<? extends TaskInfoQuery<?, ?>, ? extends TaskInfo> prepairTaskInfoQuery(
			Project pro, String assignee,
			TaskInfoQuery<? extends TaskInfoQuery<?, ?>, ? extends TaskInfo> q) {
		pro = proDao.reattachIfNeeded(pro, pro.getId());
		if (pro == null) {
			log.warn("Filtering tasks for a non-existing project {} ", pro);
			return null;
		}

		Long orgId = pro.getOrg().getId();
		Long proId = pro.getId();
		q.taskTenantId(String.valueOf(orgId))
				.taskCategory(String.valueOf(proId)).taskAssignee(assignee);
		return q;
	}

	public List<Task> filterTask(Long projectId, int first, int max) {
		Project proj = getProject(projectId);
		if (proj == null) {
			throw new ResourceNotFoundException("No such project");
		}
		return filterTask(proj, first, max);
	}

	public long countTask(Long projectId) {
		Project proj = getProject(projectId);
		if (proj == null) {
			throw new ResourceNotFoundException("No such project");
		}
		TaskQuery q = (TaskQuery) prepairTaskInfoQuery(proj,
				taskSer.createTaskQuery());
		if (q == null) {
			return 0;
		} else {
			return q.count();
		}
	}

	@SuppressWarnings("unchecked")
	public List<Task> filterTask(Project pro, int first, int max) {
		TaskQuery q = (TaskQuery) prepairTaskInfoQuery(pro,
				taskSer.createTaskQuery());
		if (q == null) {
			return Collections.EMPTY_LIST;
		} else {
			return (List<Task>) q.listPage(first, max);
		}
	}

	public List<Task> filterTask(Long proId, String assignee, int first, int max) {
		Project proj = getProject(proId);
		if (proj == null) {
			throw new ResourceNotFoundException("No such project");
		}
		return filterTask(proj, assignee, first, max);
	}

	public long countTask(Long proId, String assignee) {
		Project proj = getProject(proId);
		if (proj == null) {
			throw new ResourceNotFoundException("No such project");
		}
		TaskQuery q = (TaskQuery) prepairTaskInfoQuery(proj, assignee,
				taskSer.createTaskQuery());
		if (q == null) {
			return 0;
		} else {
			return q.count();
		}
	}

	@SuppressWarnings("unchecked")
	public List<Task> filterTask(Project pro, String assignee, int first,
			int max) {
		TaskQuery q = (TaskQuery) prepairTaskInfoQuery(pro, assignee,
				taskSer.createTaskQuery());
		if (q == null) {
			return Collections.EMPTY_LIST;
		} else {
			return (List<Task>) q.listPage(first, max);
		}
	}

	/**
	 * Query finished task.
	 * 
	 * @param pro
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<HistoricTaskInstance> filterArchTask(Project pro, int first,
			int max) {
		HistoricTaskInstanceQuery q = (HistoricTaskInstanceQuery) prepairTaskInfoQuery(
				pro, hiSer.createHistoricTaskInstanceQuery());
		if (q == null) {
			return Collections.EMPTY_LIST;
		} else {
			q.orderByTaskCreateTime().desc();
			return (List<HistoricTaskInstance>) q.finished().listPage(first,
					max);
		}
	}

	public long countArchTask(Project pro) {
		HistoricTaskInstanceQuery q = (HistoricTaskInstanceQuery) prepairTaskInfoQuery(
				pro, hiSer.createHistoricTaskInstanceQuery());
		if (q == null) {
			return 0;
		} else {
			return q.finished().count();
		}
	}

	@SuppressWarnings("unchecked")
	public List<HistoricTaskInstance> filterArchTask(Project pro,
			String assignee) {
		HistoricTaskInstanceQuery q = (HistoricTaskInstanceQuery) prepairTaskInfoQuery(
				pro, assignee, taskSer.createTaskQuery());
		if (q == null) {
			return Collections.EMPTY_LIST;
		} else {
			return (List<HistoricTaskInstance>) q.finished().list();
		}
	}

	public void deleteProject(Project pro) {
		List<Task> tasks = filterTask(pro, 0, Integer.MAX_VALUE);
		for (Task task : tasks) {
			deleteTask(task);
		}
		pro = proDao.reattachIfNeeded(pro, pro.getId());
		proDao.remove(pro);
		log.info("Deleted project {}", pro);
	}

	public Task createRecoveryTask(String taskId) {
		HistoricTaskInstance hiTask = getArchTaskWithVars(taskId);
		if (hiTask == null) {
			throw new ResourceNotFoundException("No such archived task");
		}
		if (hiTask.getEndTime() == null || hiTask.getDeleteReason() == null) {
			throw new AttempBadStateException("Task not ended yet.");
		}

		// we only recovers standalone tasks
		if (hiTask.getProcessInstanceId() != null) {
			throw new AttempBadStateException("Unable to recover process task.");
		}

		Task task = taskSer.newTask(hiTask.getId());
		task.setTenantId(hiTask.getTenantId());
		task.setName(hiTask.getName());
		task.setDescription(hiTask.getDescription());
		task.setAssignee(hiTask.getAssignee());
		task.setCategory(hiTask.getCategory());
		task.setOwner(hiTask.getOwner());
		task.setDueDate(hiTask.getDueDate());
		task.setParentTaskId(hiTask.getParentTaskId());
		task.setPriority(hiTask.getPriority());
		((TaskEntity) task).setCreateTime(hiTask.getCreateTime());
		Map<String, Object> vars = hiTask.getTaskLocalVariables();

		recoverByUpdateHistoricTask(hiTask, task);

		if (vars != null && !vars.isEmpty()) {
			taskSer.setVariables(taskId, vars);
		}

		return task;
	}

	// @SuppressWarnings("unused")
	private void recoverByUpdateHistoricTask(HistoricTaskInstance hiTask,
			Task task) {
		mgmtService.executeCommand(new CreateRecoveryTaskCmd(task));
		// update the HistoricTaskInstance
		NativeHistoricTaskInstanceQuery q = hiSer
				.createNativeHistoricTaskInstanceQuery();
		StringBuilder builder = new StringBuilder("update ")
				.append(mgmtService.getTableName(hiTask.getClass()))
				.append(" set END_TIME_=null, DELETE_REASON_=null, DURATION_=null where ID_=")
				.append(hiTask.getId());
		q.sql(builder.toString());
		q.singleResult();
	}

	/**
	 * The startTime field in HistoricTaskInstance is not correct.
	 * 
	 * @param hiTask
	 * @param task
	 */
	@SuppressWarnings("unused")
	private void recoverByDeleteHistoricTask(HistoricTaskInstance hiTask,
			Task task) {
		// only delete the historic task entry without any cascading
		mgmtService.executeCommand(new DeleteHistoricTaskNoCascadeCmd(hiTask));

		taskSer.saveTask(task);
	}

}
