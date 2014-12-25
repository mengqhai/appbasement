package com.workstream.core.service;

import java.util.Collection;

import org.activiti.engine.IdentityService;
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
public class CoreFacadeServiceTest {

	@Autowired
	private CoreFacadeService core;

	@Autowired
	private IdentityService idService;

	String userId = "mqhnow1@sina.com";

	String orgIdentifier = "coreFacadeOrg";

	@Before
	@Transactional(value = CoreConstants.TX_MANAGER, propagation = Propagation.REQUIRES_NEW)
	public void before() {
		TestUtils
				.clearUser(userId, core.getUserService(), core.getOrgService());
		TestUtils.clearOrg(orgIdentifier, core);
		TestUtils.clearOrphanGroups(idService);
	}

	@Test
	public void testCreateOrg() {
		core.getUserService().createUser(userId, "Core Facade", "passw0rd");
		UserX userX = core.getUserService().getUserX(userId);
		Organization org = core.createInitOrg(userX,
				"A test org for core facade", orgIdentifier, null);
		Assert.assertTrue(core.getOrgService().isUserInOrg(userX, org));
		Collection<GroupX> groupXes = core.getUserService().filterGroupX(org);
		Assert.assertEquals(2, groupXes.size());
		for (GroupX groupX : groupXes) {
			Assert.assertTrue(core.getUserService().isUserInGroup(userId,
					groupX.getGroupId()));
		}

		core.userLeaveOrg(userX, org);
		for (GroupX groupX : groupXes) {
			Assert.assertFalse(core.getUserService().isUserInGroup(userId,
					groupX.getGroupId()));
		}
		Assert.assertFalse(core.getOrgService().isUserInOrg(userX, org));
	}

	@Test
	public void testClearOrg() {
		core.getUserService().createUser(userId, "Core Facade", "passw0rd");
		UserX userX = core.getUserService().getUserX(userId);
		Organization org = core.createInitOrg(userX,
				"A test org for core facade", orgIdentifier, null);
		core.clearOrg(org);

		userX = core.getUserService().getUserX(userId);
		Assert.assertNotNull(userX);
		User user = core.getUserService().getUser(userId);
		Assert.assertNotNull(user);
	}

}
