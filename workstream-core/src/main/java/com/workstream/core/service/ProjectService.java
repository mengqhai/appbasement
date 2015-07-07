package com.workstream.core.service;

import static com.workstream.core.model.ProjectMembership.ProjectMembershipType.ADMIN;
import static com.workstream.core.model.ProjectMembership.ProjectMembershipType.GUEST;
import static com.workstream.core.model.ProjectMembership.ProjectMembershipType.PARTICIPANT;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

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
import com.workstream.core.exception.BadArgumentException;
import com.workstream.core.exception.BeanPropertyException;
import com.workstream.core.exception.ResourceNotFoundException;
import com.workstream.core.model.Organization;
import com.workstream.core.model.Project;
import com.workstream.core.model.Project.ProjectVisibility;
import com.workstream.core.model.ProjectMembership;
import com.workstream.core.model.ProjectMembership.ProjectMembershipType;
import com.workstream.core.model.TaskList;
import com.workstream.core.model.UserX;
import com.workstream.core.persistence.IOrganizationDAO;
import com.workstream.core.persistence.IProjectDAO;
import com.workstream.core.persistence.IProjectMembershipDAO;
import com.workstream.core.persistence.ITaskListDAO;
import com.workstream.core.persistence.IUserXDAO;
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
	private IProjectMembershipDAO memDao;

	@Autowired
	private IUserXDAO userDao;

	@Autowired
	private ITaskListDAO taskListDao;

	public static final EnumSet<ProjectMembershipType> CAPABLE_FOR_TASK_RETRIEVE = EnumSet
			.of(ADMIN, PARTICIPANT, GUEST);

	public static final EnumSet<ProjectMembershipType> CAPABLE_FOR_TASK_UPDATE = EnumSet
			.of(ADMIN, PARTICIPANT);

	public static final EnumSet<ProjectMembershipType> CAPABLE_FOR_PROJECT_UPDATE = EnumSet
			.of(ADMIN);

	public static final EnumSet<ProjectMembershipType> CAPABLE_FOR_PROJECT_MEM_UPDATE = EnumSet
			.of(ADMIN);

	public static final EnumSet<ProjectMembershipType> CAPABLE_FOR_PROJECT_MEM_RETRIEVE = EnumSet
			.of(ADMIN, PARTICIPANT);

	public Project createProject(Organization org, String name, String creatorId) {
		return createProject(org, name, creatorId, null, null, null);
	}

	public Project createProject(Organization org, String name,
			String creatorId, Date startTime, Date dueTime, String description) {
		if (creatorId == null) {
			throw new BadArgumentException("Null creator id");
		}

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

		ProjectMembership membership = new ProjectMembership();
		membership.setOrg(org);
		membership.setProject(pro);
		membership.setType(ProjectMembershipType.ADMIN);
		membership.setUserId(creatorId);
		memDao.persist(membership);

		log.debug("Created project {}.", pro);
		return pro;
	}

	public ProjectMembership checkCreateMembership(Long projectId,
			String userId, ProjectMembershipType type) {
		Project project = getProject(projectId);
		Long count = memDao.countForUserAndProject(userId, project);
		if (count > 0) {
			throw new AttempBadStateException(
					"User is already a member of the project.");
		}

		Organization org = project.getOrg();
		UserX user = userDao.findByUserId(userId);
		if (!org.getUsers().contains(user)) {
			throw new AttempBadStateException(
					"User is not a member of the organization of the project.");
		}

		ProjectMembership membership = new ProjectMembership();
		membership.setOrg(org);
		membership.setProject(project);
		membership.setType(type);
		membership.setUserId(userId);
		memDao.persist(membership);
		return membership;
	}

	public void deleteMembership(Long projectMembershipId) {
		ProjectMembership mem = memDao.findById(projectMembershipId);
		if (mem == null) {
			return;
		}
		memDao.remove(mem);
	}

	public ProjectMembership getProjectMembership(Long projectMembershipId) {
		ProjectMembership mem = memDao.findById(projectMembershipId);
		return mem;
	}

	public ProjectMembership getProjectMembership(String userId, Long projectId) {
		Project pro = proDao.getReference(projectId);
		ProjectMembership mem = memDao.getProjectMemebership(userId, pro);
		return mem;
	}

	public ProjectMembership updateProjectMembership(Long projectMembershipId,
			ProjectMembershipType type) {
		ProjectMembership mem = getProjectMembership(projectMembershipId);
		if (mem == null) {
			throw new ResourceNotFoundException("No such membership.");
		}
		mem.setType(type);
		return mem;
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

	/**
	 * Get the projects in an org that are visible to the given user
	 * 
	 * 
	 * @param first
	 * @param max
	 * @return
	 */
	public Collection<Project> filterProjectByOrgAndUser(Organization org,
			String userId) {
		return proDao.filterForUserVisiableInOrg(org, userId);
	}

	public Collection<ProjectMembership> filterProjectMemberships(
			Long projectId, int first, int max) {
		Project project = proDao.getReference(projectId);
		return memDao.filterFor(project, first, max);
	}

	public Collection<ProjectMembership> filterProjectMembershipsByUser(
			String userId, int first, int max) {
		return memDao.filterForUser(userId, first, max);
	}

	public boolean checkMembershipCapability(String userId, Long projectId,
			EnumSet<ProjectMembershipType> capableMembershipSet) {
		Project project = getProject(projectId);
		if (project == null) {
			throw new ResourceNotFoundException("No such project");
		}
		if (ProjectVisibility.OPEN != project.getVisibility()) {
			ProjectMembership mem = memDao.getProjectMemebership(userId,
					project);
			return (mem != null && capableMembershipSet.contains(mem.getType()));
		} else {
			return true;
		}
	}

	public boolean checkMembershipForProjectUpdate(String userId, Long projectId) {
		return checkMembershipCapability(userId, projectId,
				CAPABLE_FOR_PROJECT_UPDATE);
	}

	public boolean checkMembershipForProjectMemUpdate(String userId,
			Long projectId) {
		return checkMembershipCapability(userId, projectId,
				CAPABLE_FOR_PROJECT_MEM_UPDATE);
	}

	public boolean checkMembershipForProjectMemRetrieve(String userId,
			Long projectId) {
		return checkMembershipCapability(userId, projectId,
				CAPABLE_FOR_PROJECT_MEM_RETRIEVE);
	}

	public boolean checkMembershipForTaskUpdate(String userId, Long projectId) {
		return checkMembershipCapability(userId, projectId,
				CAPABLE_FOR_TASK_UPDATE);
	}

	public boolean checkMembershipForTaskRetrieve(String userId, Long projectId) {
		return checkMembershipCapability(userId, projectId,
				CAPABLE_FOR_TASK_RETRIEVE);
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
		q.taskTenantId(String.valueOf(orgId))
				.taskCategory(String.valueOf(proId)).orderByTaskCreateTime()
				.desc();
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

	public Collection<TaskList> filterTaskList(Project pro) {
		return taskListDao.filterFor(pro, 0, Integer.MAX_VALUE);
	}

	public List<Task> filterTaskForTaskList(Long taskListId) {
		return taskSer.getSubTasks("list|" + taskListId);
	}

	public Collection<TaskList> filterTaskList(Long projectId) {
		Project pro = proDao.findById(projectId);
		if (pro == null) {
			throw new ResourceNotFoundException("No such project.");
		}
		return this.filterTaskList(pro);
	}

	public TaskList getTaskList(Long taskListId) {
		TaskList taskList = taskListDao.findById(taskListId);
		return taskList;
	}

	public void updateTaskList(Long taskListId, Map<String, Object> patch) {
		TaskList taskList = taskListDao.findById(taskListId);
		if (taskList == null) {
			throw new ResourceNotFoundException("No such task list");
		}
		try {
			BeanUtils.copyProperties(taskList, patch);
			// BeanUtils.populate() got exception No value specified for 'Date'
			// when dueDate == null
			// http://www.blogjava.net/javagrass/archive/2011/10/10/352856.html
		} catch (Exception e) {
			log.error("Failed to populate the props to taskList: {}", patch, e);
			throw new BeanPropertyException(e);
		}
	}

	public void addTaskToList(Long taskListId, String taskId) {
		TaskList taskList = taskListDao.findById(taskListId);
		if (taskList == null) {
			throw new ResourceNotFoundException("No such task list");
		}
		Task task = this.getTask(taskId);
		if (task == null) {
			throw new ResourceNotFoundException("No such task");
		}
		if (task.getParentTaskId() != null) {
			throw new AttempBadStateException("Task already has a parent");
		}
		taskList.getTaskIds().add(task.getId());
		task.setParentTaskId("list|" + taskList.getId());
		taskSer.saveTask(task);
	}

	public void removeTaskFromList(Long taskListId, String taskId) {
		TaskList taskList = taskListDao.findById(taskListId);
		if (taskList == null) {
			throw new ResourceNotFoundException("No such task list");
		}
		Task task = this.getTask(taskId);
		if (task == null) {
			throw new ResourceNotFoundException("No such task");
		}
		taskList.getTaskIds().remove(task.getId());
		String oldTaskListIdStr = task.getParentTaskId();
		if (oldTaskListIdStr != null
				&& !oldTaskListIdStr.equals("list|" + taskListId)) {
			throw new AttempBadStateException("Task is not in the task list");
		}
		task.setParentTaskId(null);
		taskSer.saveTask(task);
	}

	public TaskList createTaskList(Long projectId, TaskList taskList) {
		Project pro = proDao.findById(projectId);
		if (pro == null) {
			throw new ResourceNotFoundException("No such project");
		}
		taskList.setOrg(pro.getOrg());
		taskList.setProject(pro);
		taskListDao.persist(taskList);
		return taskList;
	}

	public TaskList createTaskList(Long projectId,
			Map<String, Object> taskListProps) {
		TaskList taskList = new TaskList();
		try {
			BeanUtils.populate(taskList, taskListProps);
		} catch (Exception e) {
			log.error("Failed to populate the props to taskList: {}",
					taskListProps, e);
			throw new BeanPropertyException(e);
		}
		return this.createTaskList(projectId, taskList);
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
		Collection<ProjectMembership> memberships = memDao.filterFor(pro, 0,
				Integer.MAX_VALUE);
		for (ProjectMembership mem : memberships) {
			memDao.remove(mem);
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
