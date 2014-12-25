package com.workstream.core.service;

import java.util.Collection;
import java.util.List;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;

import com.workstream.core.model.Organization;
import com.workstream.core.model.UserX;

public class TestUtils {

	public static void clearUser(String userId, UserService userService,
			OrganizationService orgService) {
		UserX userX = userService.getUserX(userId);
		if (userX != null) {
			Collection<Organization> orgs = orgService.filterOrg(userX);
			for (Organization org : orgs) {
				orgService.userLeaveOrg(userX, org);
			}
		}
		userService.removeUser(userId);
	}

	public static void clearOrg(String orgIdentifier, CoreFacadeService core) {
		Organization org = core.getOrgService().findOrgByIdentifier(
				orgIdentifier);
		if (org != null) {
			core.clearOrg(org);
		}
	}

	public static void clearOrphanGroups(IdentityService idService) {
		List<Group> groups = idService
				.createNativeGroupQuery()
				.sql("select * from ACT_ID_GROUP as g where not exists (select * from WS_GROUP as w where g.id_=w.groupId)  and g.id_ like '%|%'")
				.list();
		for (Group group : groups) {
			idService.deleteGroup(group.getId());
		}
	}

}
