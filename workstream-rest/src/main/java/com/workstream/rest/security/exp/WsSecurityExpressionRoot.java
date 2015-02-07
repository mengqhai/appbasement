package com.workstream.rest.security.exp;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.activiti.engine.identity.Group;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.workstream.core.exception.ResourceNotFoundException;
import com.workstream.core.model.Organization;
import com.workstream.core.model.UserX;
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
		if (orgId == null) {
			return true; // system task
		}
		return isAuthInOrg(orgId);
	}

	public boolean isAuthInOrgForUser(String userId) {
		Collection<String> orgIds = getOrgIdsFromUser(userId);
		for (String orgId : orgIds) {
			if (isAuthInOrg(orgId)) {
				return true;
			}
		}
		return false;
	}

	public String getOrgIdFromTask(String taskId) {
		Task task = CoreFacadeService.getInstance().getProjectService()
				.getTask(taskId);
		if (task == null) {
			throw new ResourceNotFoundException("No such task");
		}
		return task.getTenantId();
	}

	public Set<String> getOrgIdsFromUser(String userId) {
		UserX userX = CoreFacadeService.getInstance().getUserService()
				.getUserX(userId);
		if (userX == null) {
			throw new ResourceNotFoundException("No such user");
		}
		Collection<Organization> orgList = CoreFacadeService.getInstance()
				.getOrgService().filterOrg(userX);
		Set<String> orgIds = new HashSet<String>(orgList.size());
		for (Organization org : orgList) {
			orgIds.add(String.valueOf(org.getId()));
		}
		return orgIds;
	}

	protected List<Group> getGroups(String userId) {
		return CoreFacadeService.getInstance().getUserService()
				.filterGroupByUser(userId);
	}

	public List<Group> getGroups(String userId, GroupType type) {
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
