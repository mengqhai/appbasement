package com.workstream.rest.security.exp;

import java.util.Collection;
import java.util.List;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.workstream.core.exception.ResourceNotFoundException;
import com.workstream.core.model.Project;
import com.workstream.core.service.CoreFacadeService;
import com.workstream.core.service.UserService.GroupType;
import com.workstream.rest.utils.RestUtils;

public class WsSecurityExpressionRoot extends SecurityExpressionRoot implements
		MethodSecurityExpressionOperations {

	private final static Logger log = LoggerFactory
			.getLogger(WsSecurityExpressionRoot.class);

	private Object filterObject;
	private Object returnObject;
	private Object target;

	public WsSecurityExpressionRoot(Authentication a) {
		super(a);
	}

	public String decodeUserId(String userIdBase64) {
		try {
			return RestUtils.decodeUserId(userIdBase64);
		} catch (Exception e) {
			log.warn("Failed to decode user id, {}", e.getMessage());
			return null;
		}
	}

	public boolean isAuthInOrg(String orgId) {
		Collection<? extends GrantedAuthority> authorities = getAuthentication()
				.getAuthorities();
		for (GrantedAuthority a : authorities) {
			String role = a.getAuthority();
			if (role != null && role.startsWith(orgId + "|")) {
				return true;
			}
		}

		// No longer needed as DynamicAuthenticationToken is used
		// worst case, need to check actual group
		// List<Group> groups = getGroups((String) auth.getPrincipal());
		// for (Group group : groups) {
		// if (group.getId().startsWith(orgId + "|")) {
		// return true;
		// }
		// }
		return false;
	}

	public boolean isAuthInGroup(String groupId) {
		Collection<? extends GrantedAuthority> authorities = this
				.getAuthentication().getAuthorities();
		for (GrantedAuthority a : authorities) {
			String role = a.getAuthority();
			if (role != null && role.equals(groupId)) {
				return true;
			}
		}
		return false;
	}

	public boolean isAuthInOrgForGroup(String groupId) {
		String orgId = RestUtils.getOrgIdFromGroupId(groupId);
		return isAuthInOrg(orgId);
	}

	public boolean isAuthInOrgForTask(String taskId) {
		String orgId = getOrgIdFromTask(taskId);
		if (orgId == null || orgId.isEmpty()) {
			return true; // system task
		}
		return isAuthInOrg(orgId);
	}

	public boolean isAuthInOrgForProcess(String processId) {
		String orgId = getOrgIdFromProcess(processId);
		if (orgId == null || orgId.isEmpty()) {
			return true; // system process
		}
		return isAuthInOrg(orgId);
	}

	public boolean isAuthInOrgForTemplate(String templateId) {
		String orgId = getOrgIdFromTemplate(templateId);
		if (orgId == null) {
			return true; // system template
		}
		return isAuthInOrg(orgId);
	}

	public boolean isAuthInOrgForModel(String modelId) {
		String orgId = getOrgIdFromModel(modelId);
		if (orgId == null) {
			return true; // system model
		}
		return isAuthInOrg(orgId);
	}

	public boolean isAuthAdminForModel(String modelId) {
		String orgId = getOrgIdFromModel(modelId);
		if (orgId == null) {
			return true; // system model
		}
		return isAuthAdmin(orgId);
	}

	public boolean isAuthProcessDesignerForModel(String modelId) {
		String orgId = getOrgIdFromModel(modelId);
		if (orgId == null) {
			return true; // system model
		}
		return isAuthProcessDesigner(orgId);
	}

	public boolean isAuthAdminForTemplate(String templateId) {
		String orgId = getOrgIdFromTemplate(templateId);
		if (orgId == null) {
			return true; // system template
		}
		return isAuthAdmin(orgId);
	}

	public boolean isAuthInOrgForUser(String userId) {
		Collection<String> orgIds = CoreFacadeService.getInstance()
				.getOrgIdsFromUser(userId);
		for (String orgId : orgIds) {
			if (isAuthInOrg(orgId)) {
				return true;
			}
		}
		return false;
	}

	public boolean isAuthInOrgForArchTask(String taskId) {
		String orgId = getOrgIdFromArchTask(taskId);
		if (orgId == null) {
			return true; // system task
		}
		return isAuthInOrg(orgId);
	}

	public boolean isAuthInOrgForArchProcess(String processId) {
		String orgId = getOrgIdFromArchProcess(processId);
		if (orgId == null || "".equals(orgId)) {
			return true; // system task
		}
		return isAuthInOrg(orgId);
	}

	public boolean isAuthInOrgForProject(Long projectId) {
		String orgId = getOrgIdFromProject(projectId);
		return isAuthInOrg(orgId);
	}

	public boolean isAuthInOrgForAttachment(String attachmentId) {
		String orgId = getOrgIdFromAttachment(attachmentId);
		if (orgId != null) {
			return isAuthInOrg(orgId);
		}
		return false;
	}

	protected String getOrgIdFromTask(String taskId) {
		Task task = CoreFacadeService.getInstance().getProjectService()
				.getTask(taskId);
		if (task == null) {
			throw new ResourceNotFoundException("No such task");
		}
		return task.getTenantId();
	}

	protected String getOrgIdFromProject(Long projectId) {
		Project pro = CoreFacadeService.getInstance().getProjectService()
				.getProject(projectId);
		if (pro == null) {
			throw new ResourceNotFoundException("No such project");
		}
		return String.valueOf(pro.getOrg().getId());
	}

	protected String getOrgIdFromTemplate(String templateId) {
		ProcessDefinition pd = CoreFacadeService.getInstance()
				.getTemplateService().getProcessTemplate(templateId);
		if (pd == null) {
			throw new ResourceNotFoundException("No such template");
		}
		return pd.getTenantId();
	}

	protected String getOrgIdFromArchTask(String taskId) {
		HistoricTaskInstance archTask = CoreFacadeService.getInstance()
				.getProjectService().getArchTask(taskId);
		if (archTask == null) {
			throw new ResourceNotFoundException("No such task");
		}
		return archTask.getTenantId();
	}

	protected String getOrgIdFromArchProcess(String processId) {
		HistoricProcessInstance archTask = CoreFacadeService.getInstance()
				.getProcessService().getHiProcess(processId);
		if (archTask == null) {
			throw new ResourceNotFoundException("No such process");
		}
		return archTask.getTenantId();
	}

	protected String getOrgIdFromModel(String modelId) {
		Model model = CoreFacadeService.getInstance().getModel(modelId);
		if (model == null) {
			throw new ResourceNotFoundException("No such model");
		}
		return model.getTenantId();
	}

	protected String getOrgIdFromAttachment(String attachment) {
		Attachment att = CoreFacadeService.getInstance().getAttachmentService()
				.getAttachment(attachment);
		if (att == null) {
			throw new ResourceNotFoundException("No such attachment");
		}
		String desc = att.getDescription();
		String orgId = RestUtils.getOrgIdFromAttachmentDesc(desc);
		return orgId;
	}

	protected String getOrgIdFromProcess(String processId) {
		ProcessInstance pi = CoreFacadeService.getInstance()
				.getProcessService().getProcess(processId);
		if (pi == null) {
			throw new ResourceNotFoundException("No such process");
		}
		return pi.getTenantId();
	}

	protected List<Group> getGroups(String userId) {
		return CoreFacadeService.getInstance().getUserService()
				.filterGroupByUser(userId);
	}

	protected List<Group> getGroups(String userId, GroupType type) {
		return CoreFacadeService.getInstance().getUserService()
				.filterGroupByUser(userId, type);
	}

	protected boolean isAuthOfGroupType(String orgId, GroupType type) {
		Collection<? extends GrantedAuthority> authorities = getAuthentication()
				.getAuthorities();
		for (GrantedAuthority a : authorities) {
			String role = a.getAuthority();
			if (role != null && role.startsWith(orgId + "|")) {
				Group group = CoreFacadeService.getInstance().getUserService()
						.getGroup(role);
				if (type.toString().endsWith(group.getType())) {
					return true;
				}
			}
		}

		// No longer needed as DynamicAuthenticationToken is used
		// worst case, need to check actual group
		// List<Group> groups = getGroups((String) auth.getPrincipal(), type);
		// for (Group group : groups) {
		// if (group.getId().startsWith(orgId + "|")) {
		// return true;
		// }
		// }
		return false;
	}

	public boolean isAuthProcessDesigner(String orgId) {
		return isAuthOfGroupType(orgId, GroupType.PROCESS_DESIGNER);
	}

	public boolean isAuthAdmin(String orgId) {
		return isAuthOfGroupType(orgId, GroupType.ADMIN);
	}

	public boolean isAuthAdminForGroup(String groupId) {
		String orgId = RestUtils.getOrgIdFromGroupId(groupId);
		return isAuthAdmin(orgId);
	}

	public boolean isAuthAdminForProject(Long projectId) {
		String orgId = getOrgIdFromProject(projectId);
		return isAuthAdmin(orgId);
	}

	@Override
	public void setFilterObject(Object filterObject) {
		this.filterObject = filterObject;
	}

	@Override
	public Object getFilterObject() {
		return filterObject;
	}

	@Override
	public void setReturnObject(Object returnObject) {
		this.returnObject = returnObject;
	}

	@Override
	public Object getReturnObject() {
		return returnObject;
	}

	@Override
	public Object getThis() {
		return target;
	}

}
