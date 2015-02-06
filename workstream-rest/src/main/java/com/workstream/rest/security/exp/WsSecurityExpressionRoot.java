package com.workstream.rest.security.exp;

import java.util.Collection;
import java.util.List;

import org.activiti.engine.identity.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

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

	public boolean isAuthInOrg(Authentication auth, String orgId) {
		Collection<? extends GrantedAuthority> authorities = auth
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

	protected List<Group> getGroups(String userId) {
		return CoreFacadeService.getInstance().getUserService()
				.filterGroupByUser(userId);
	}

	public List<Group> getGroups(String userId, GroupType type) {
		return CoreFacadeService.getInstance().getUserService()
				.filterGroupByUser(userId, type);
	}

	protected boolean isAuthOfGroupType(Authentication auth, String orgId,
			GroupType type) {
		Collection<? extends GrantedAuthority> authorities = auth
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

	public boolean isAuthProcessDesigner(Authentication auth, String orgId) {
		return isAuthOfGroupType(auth, orgId, GroupType.PROCESS_DESIGNER);
	}

	public boolean isAuthAdmin(Authentication auth, String orgId) {
		return isAuthOfGroupType(auth, orgId, GroupType.ADMIN);
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
