package com.workstream.core.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.CoreConstants;
import com.workstream.core.conf.ApplicationConfiguration;
import com.workstream.core.model.GroupX;
import com.workstream.core.model.Organization;
import com.workstream.core.model.UserX;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfiguration.class)
@TransactionConfiguration(transactionManager = CoreConstants.TX_MANAGER, defaultRollback = false)
public class UserServiceTest {
	@Autowired
	UserService service;

	@Autowired
	OrganizationService orgService;

	String userId = "mqhnow1@sina.com";

	@Before
	@Transactional(value = CoreConstants.TX_MANAGER, propagation = Propagation.REQUIRES_NEW)
	public void before() {
		UserX userX = service.getUserX(userId);
		if (userX != null) {
			Collection<Organization> orgs = orgService.filterOrg(userX);
			for (Organization org : orgs) {
				orgService.userLeaveOrg(userX, org);
			}
			service.removeUser(userId);
		}
	}

	@Test
	public void testCreateUser() {
		service.createUser(userId, "孟庆华", "passw0rd");
		User user = service.getUser(userId);
		Assert.assertEquals(userId, user.getEmail());
		Assert.assertEquals("passw0rd", user.getPassword());
		Assert.assertEquals("孟庆华", user.getFirstName());
	}

	@Test
	public void testUpdateUser() {
		service.deleteUserX(userId);
		service.createUser(userId, "孟庆华", "passw0rd");
		Map<String, String> props = new HashMap<String, String>();
		props.put("firstName", "Mikkie");
		props.put("password", "Welcome1");
		service.updateUser(userId, props);

		User user = service.getUser(userId);
		Assert.assertEquals("Mikkie", user.getFirstName());
		Assert.assertEquals("Welcome1", user.getPassword());
	}

	@Test
	@Transactional(value = CoreConstants.TX_MANAGER, propagation = Propagation.REQUIRED)
	public void testCreateGroup() {
		Organization org = orgService.createOrg("group test org",
				"groupTestOrg", null);
		Group group1 = service.createGroup(org, "test group 1",
				"Hello test group");
		Group group2 = service.createGroup(org, "test group 2",
				"Hello test group");

		Group group1Created = service.getGroup(group1.getId());
		Assert.assertEquals(group1.getName(), group1Created.getName());
		Assert.assertEquals(group1.getType(), group1Created.getType());
		GroupX groupX1Created = service.getGroupX(group1.getId());
		Assert.assertEquals("Hello test group", groupX1Created.getDescription());
		Assert.assertEquals(org.getId(), groupX1Created.getOrg().getId());
		Assert.assertEquals(org, groupX1Created.getOrg());

		Group group2Created = service.getGroup(group2.getId());
		Assert.assertEquals(group2.getName(), group2Created.getName());
		Assert.assertEquals(group2.getType(), group2Created.getType());
		GroupX groupX2Created = service.getGroupX(group2.getId());
		Assert.assertEquals(groupX2Created.getDescription(), "Hello test group");
		Assert.assertEquals(org.getId(), groupX2Created.getOrg().getId());
		Assert.assertEquals(org, groupX2Created.getOrg());

		// get all groups for the org
		Collection<GroupX> groupXes = service.filterGroupX(org);
		Assert.assertEquals(2, groupXes.size());
		groupXes.contains(groupX1Created);
		groupXes.contains(groupX2Created);

		List<Group> groups = service.filterGroup(org);
		Assert.assertEquals(2, groups.size());
		Set<String> ids = new HashSet<String>();
		ids.add(groups.get(0).getId());
		ids.add(groups.get(1).getId());
		Assert.assertTrue(ids.contains(group1.getId()));
		Assert.assertTrue(ids.contains(group2.getId()));

		// test delete
		for (GroupX groupX : groupXes) {
			service.removeGroup(groupX);
		}

		Assert.assertEquals(0, service.filterGroupX(org).size());
		Assert.assertEquals(0, service.filterGroup(org).size());
		Assert.assertNull(service.getGroupX(groupX1Created.getId()));
		Assert.assertNull(service.getGroupX(groupX2Created.getId()));
		orgService.removeOrg(org); // cascade removes org & groupXes

	}

	@Test
	public void testCreateRemoveGroup() {
		Organization org = orgService.createOrg("group test org",
				"groupTestOrg", null);
		Group group1 = service.createGroup(org, "test group 1",
				"Hello test group");
		GroupX groupX1Created = service.getGroupX(group1.getId());
		service.removeGroup(groupX1Created);
		Assert.assertNull(service.getGroupX(groupX1Created.getId()));
		Assert.assertEquals(0, service.filterGroup(org).size());
		Assert.assertEquals(0, service.filterGroupX(org).size());
	}

	@Test
	public void testUserOrganization() {
		service.createUser(userId, "孟庆华", "passw0rd");
		Organization org = orgService.createOrg("user join test org",
				"userJoinTestOrg", null);
		UserX userX = service.getUserX(userId);
		orgService.userJoinOrg(userX, org);

		Organization orgLoaded = orgService.findOrgByIdEagerUsers(org.getId());
		Assert.assertEquals(1, orgLoaded.getUsers().size());
		Assert.assertTrue(orgLoaded.getUsers().contains(userX));

		// test filter user by org
		Collection<UserX> userXes = service.filterUserX(org);
		Assert.assertEquals(1, userXes.size());
		Assert.assertTrue(userXes.contains(userX));

		List<User> users = service.filterUser(org);
		Assert.assertEquals(1, users.size());
		Assert.assertEquals(userId, users.get(0).getId());
	}

	@Test(expected = RuntimeException.class)
	public void testGroupUserFail() {
		Organization org = orgService.createOrg("group user test org",
				"groupUserTestOrg", null);
		Group group1 = service.createGroup(org, "test group 1",
				"Hello test group");
		service.createUser(userId, "孟庆华", "passw0rd");
		service.addUserToGroup(userId, group1.getId());

		List<User> users = service.filterUserByGroupId(group1.getId());
		Assert.assertEquals(1, users.size());
		Assert.assertEquals(userId, users.get(0).getId());
	}

	@Test
	public void testGroupUser() {
		Organization org = orgService.createOrg("group user test org",
				"groupUserTestOrg", null);
		Group group1 = service.createGroup(org, "test group 1",
				"Hello test group");
		service.createUser(userId, "孟庆华", "passw0rd");
		UserX createdUser = service.getUserX(userId);

		orgService.userJoinOrg(createdUser, org);

		service.addUserToGroup(userId, group1.getId());

		List<User> users = service.filterUserByGroupId(group1.getId());
		Assert.assertEquals(1, users.size());
		Assert.assertEquals(userId, users.get(0).getId());
	}
}
