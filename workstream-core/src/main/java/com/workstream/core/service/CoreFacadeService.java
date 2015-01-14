package com.workstream.core.service;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.CoreConstants;
import com.workstream.core.exception.AttempBadStateException;
import com.workstream.core.exception.AuthenticationNotSetException;
import com.workstream.core.exception.BadArgumentException;
import com.workstream.core.exception.DataBadStateException;
import com.workstream.core.exception.ResourceNotFoundException;
import com.workstream.core.model.GroupX;
import com.workstream.core.model.Organization;
import com.workstream.core.model.Project;
import com.workstream.core.model.UserX;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
public class CoreFacadeService {
	private final Logger log = LoggerFactory.getLogger(CoreFacadeService.class);

	@Autowired
	private OrganizationService orgSer;

	@Autowired
	private UserService uSer;

	@Autowired
	private ProjectService projSer;

	@Autowired
	private ProcessService procSer;

	@Autowired
	private TemplateService tempSer;

	@Autowired
	private CoreEventService eventSer;

	private static AtomicReference<CoreFacadeService> INSTANCE = new AtomicReference<CoreFacadeService>();

	protected CoreFacadeService() {
		final CoreFacadeService previous = INSTANCE.getAndSet(this);
		if (previous != null) {
			log.warn(
					"Creating a second instance of CoreFacadeService: old: {} new: {}",
					previous, this);
			if (previous.getClass() == this.getClass()) {
				// this constructor can also be called by the subclass, so only
				// throw exception when the classes are exactly the same
				throw new IllegalStateException("Second singleton " + this
						+ " created after " + previous);
			}
		}
	}

	public static CoreFacadeService getInstance() {
		return INSTANCE.get();
	}

	/**
	 * With existence check.
	 * 
	 * @param orgId
	 * @return
	 */
	public Organization getOrg(Long orgId) throws ResourceNotFoundException {
		Organization org = orgSer.findOrgById(orgId);
		if (org == null) {
			throw new ResourceNotFoundException("No such org.");
		}
		return org;
	}

	/**
	 * Get template model with existence check
	 * 
	 * @param model
	 * @return
	 */
	public Model getModel(String modelId) {
		Model model = tempSer.getModel(modelId);
		if (model == null) {
			throw new ResourceNotFoundException("No such model");
		}
		return model;
	}

	/**
	 * Delete all the deployments for a given model
	 * 
	 * @param modelId
	 */
	public void undeployModelAll(String modelId) {
		List<Deployment> deployments = tempSer
				.filterDeploymentByModelId(modelId);
		if (deployments.isEmpty()) {
			throw new BadArgumentException("The model has not deployed yet.");
		}

		for (Deployment d : deployments) {
			String deploymentId = d.getId();
			tempSer.removeDeployment(deploymentId);
			log.debug("Deployment deleted: {}", deploymentId);
		}
	}

	public void undeployModelOnlyLast(String modelId) {
		Model model = getModel(modelId);
		String lastDeployId = model.getDeploymentId();
		if (lastDeployId == null) {
			throw new BadArgumentException("The model has not deployed yet.");
		}
		tempSer.removeDeployment(lastDeployId);
		log.debug("Deployment deleted: {}", lastDeployId);
		// need to set the model's deploymentId field the the next last
		// deployment
		List<Deployment> remainings = tempSer
				.filterDeploymentByModelId(modelId);

		if (!remainings.isEmpty()) {
			// the model still has other deployments
			Deployment lastDeploy = remainings.get(0);
			// filterDeploymentByModelId already guarantees the last deployment
			// is on the head of the list
			model = getModel(modelId);
			Map<String, Object> props = new HashMap<String, Object>(1);
			props.put("deploymentId", lastDeploy.getId());
			tempSer.updateModel(modelId, props);
			log.debug("Updated the model's last deployment to {}", lastDeployId);
		}
		// lastDeployId = null;
		// Date lastDeploymentTime = null;
		// for (Deployment remaining : remainings) {
		// if (lastDeploymentTime == null
		// || lastDeploymentTime.before(remaining.getDeploymentTime())) {
		// lastDeploymentTime = remaining.getDeploymentTime();
		// lastDeployId = remaining.getId();
		// }
		// }
		// if (lastDeployId != null) {
		//
		// }
	}

	public Organization createInitOrg(UserX creator, String name,
			String identifier, String description) {
		Organization org = orgSer.createOrg(name, identifier, description);
		orgSer.userJoinOrg(creator, org);
		// create predefined groups
		Group group = uSer.createGroup(org, "Admin",
				"Adimistrators of the organization.");
		uSer.addUserToGroup(creator, group.getId());
		group = uSer.createGroup(org, "Process Designer",
				"Process Designers of the organization");
		uSer.addUserToGroup(creator, group.getId());
		return org;
	}

	public Group createGroupInOrg(Long orgId, String name, String description) {
		Organization org = getOrg(orgId);
		Group group = uSer.createGroup(org, name, description);
		return group;
	}

	public Project createProjectInOrg(Long orgId, String name, Date startTime,
			Date dueTime, String description) {
		Organization org = getOrg(orgId);
		;
		return projSer
				.createProject(org, name, startTime, dueTime, description);
	}

	public void checkTaskAssigneeOrg(String assigneeId, Organization org)
			throws AttempBadStateException {
		// check the existence of the assignee user
		if (assigneeId != null) {
			UserX userX = uSer.getUserX(assigneeId);
			if (userX == null) {
				throw new ResourceNotFoundException("No such user for assignee");
			}
			// check if the assignee is in the organization of the project
			if (!orgSer.isUserInOrg(userX, org)) {
				throw new AttempBadStateException(
						"Assignee is not in the task's org");
			}
		}
	}

	public Task createTaskInProject(Long projectId, String name,
			String description, Date dueDate, String assigneeId,
			Integer priority) {
		String creator = this.getAuthUserId();

		Project proj = projSer.getProject(projectId);
		if (proj == null) {
			throw new ResourceNotFoundException("No such project");
		}

		// check the existence of the assignee user
		checkTaskAssigneeOrg(assigneeId, proj.getOrg());

		Task task = projSer.createTask(proj, creator, name, description,
				dueDate, assigneeId, priority);
		return task;
	}

	public Group getOrgAdminGroup(Organization org)
			throws DataBadStateException {
		List<Group> groups = uSer.filterGroup(org);
		for (Group group : groups) {
			if ("Admin".equals(group.getName())) {
				return group;
			}
		}
		log.error("Org {} has no admin group!", org.getIdentifier());
		throw new DataBadStateException("Org {} has no admin group!"
				+ org.getIdentifier());
	}

	public void userLeaveOrg(UserX userX, Organization org) {
		// TODO other things like check the process templates, etc.

		// user must firstly be removed from all the groups of the org
		Collection<GroupX> groupXList = uSer.filterGroupX(org);
		for (GroupX groupX : groupXList) {
			uSer.removeUserFromGroup(userX.getUserId(), groupX.getGroupId());
		}
		orgSer.userLeaveOrg(userX, org);
	}

	/**
	 * It's a big deal to clear an org.
	 * 
	 * @param org
	 */
	public void clearOrg(Organization org) {
		// TODO remove all the task, processes, projects of the org

		// delete groups of the org
		Collection<GroupX> groupXes = uSer.filterGroupX(org);
		for (GroupX groupX : groupXes) {
			uSer.removeGroup(groupX);
		}

		// delete projects of the org
		Collection<Project> projects = projSer.filterProject(org);
		for (Project pro : projects) {
			projSer.deleteProject(pro);
		}

		orgSer.removeOrg(org); // cascade removes org & groupXes
		log.info("Cleared org id={} name={} identifier={}", org.getId(),
				org.getName(), org.getIdentifier());

		// do users needs to leave the org? No, the relationship between org and
		// user is deleted by cascading
	}

	public void deleteProject(Long projectId) {
		Project proj = projSer.getProject(projectId);
		if (proj == null) {
			throw new ResourceNotFoundException("No such project");
		}
		projSer.deleteProject(proj);
	}

	/**
	 * With additional logic the check the new assignee
	 * 
	 * @param taskId
	 * @param props
	 * 
	 * @throws AttempBadStateException
	 *             if the assignee is not in the task's org
	 * @throws ResourceNotFoundException
	 *             if the assignee is not found
	 */
	public void updateTask(String taskId, Map<String, ? extends Object> props)
			throws ResourceNotFoundException, AttempBadStateException {
		Task task = projSer.getTask(taskId);
		if (task == null) {
			throw new ResourceNotFoundException("No such task");
		}
		String assigneeId = (String) props.get("assignee");
		if (assigneeId != null && task.getTenantId() != null) {
			Long orgId = Long.valueOf(task.getTenantId());
			Organization org = getOrg(orgId);
			checkTaskAssigneeOrg(assigneeId, org);
		}
		projSer.updateTask(task, props);
	}

	public List<User> filterUserByOrgId(Long orgId) {
		Organization org = getOrg(orgId);
		return uSer.filterUser(org);
	}

	public Collection<Project> filterProjectByOrgId(Long orgId) {
		Organization org = getOrg(orgId);
		return projSer.filterProject(org);
	}

	/**
	 * If the activiti authenticatedUserId is not set, throws the
	 * AuthenticationNotSetException exception.
	 * 
	 * @return
	 * @throws AuthenticationNotSetException
	 */
	public String getAuthUserId() throws AuthenticationNotSetException {
		String userId = Authentication.getAuthenticatedUserId();
		if (userId == null) {
			throw new AuthenticationNotSetException();
		}
		return userId;
	}

	/**
	 * If the activiti authenticatedUserId is not set, throws the
	 * AuthenticationNotSetException exception.
	 * 
	 * @return
	 * @throws AuthenticationNotSetException
	 */
	public UserX getAuthUserX() throws AuthenticationNotSetException {
		String userId = getAuthUserId();
		return uSer.getUserX(userId);
	}

	public OrganizationService getOrgService() {
		return orgSer;
	}

	public UserService getUserService() {
		return uSer;
	}

	public ProcessInstance requestUserJoinOrg(Long orgId) {
		UserX userX = getAuthUserX();
		Organization org = getOrg(orgId);
		if (!orgSer.isUserInOrg(userX, org)) {
			Group adminGroup = getOrgAdminGroup(org);
			Map<String, Object> variableMap = new HashMap<String, Object>();
			variableMap.put("orgId", org.getId());
			variableMap.put("orgName", org.getName());
			variableMap.put("userId", userX.getUserId());
			variableMap.put("adminGroupId", adminGroup.getId());
			ProcessInstance pi = procSer.startProcessByKey(
					CoreConstants.PRO_KEY_USER_JOIN_ORG, variableMap);
			log.info(
					"System process {} started to handle user({}) join org({}) request.",
					pi.getId(), userX.getId(), orgId);
			return pi;
		} else {
			throw new AttempBadStateException("User already in the org");
		}
	}

	/**
	 * out the properties that are not supposed to be set in this form
	 * 
	 * @param taskId
	 * @param formProps
	 */
	public void completeTaskByForm(String taskId, Map<String, String> formProps) {
		if (formProps == null) {
			throw new BadArgumentException("No form obj");
		}
		// must filter out the properties that are not supposed to be set in
		// this form, otherwise they'll become/modify the process instance
		// variables with the same name.
		// This will also filter out the null values.
		TaskFormData formData = procSer.getTaskFormData(taskId);
		if (formData == null) {
			// the task has no form defs
			procSer.completeTask(taskId);
			return;
		}
		List<FormProperty> propDefs = formData.getFormProperties();
		Map<String, String> filteredFormProps = new HashMap<String, String>();
		for (FormProperty propDef : propDefs) {
			String key = propDef.getId();
			String value = formProps.get(key);
			if (value != null) {
				filteredFormProps.put(key, value);
			}
		}
		procSer.submitTaskFormData(taskId, filteredFormProps);
	}

	public ProjectService getProjectService() {
		return projSer;
	}

	public ProcessService getProcessService() {
		return procSer;
	}

	public TemplateService getTemplateService() {
		return tempSer;
	}

	public CoreEventService getEventService() {
		return eventSer;
	}

}
