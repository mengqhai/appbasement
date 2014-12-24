package com.workstream.core.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.workstream.core.conf.ApplicationConfiguration;
import com.workstream.core.model.Organization;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfiguration.class)
public class OrgServiceTest {

	@Autowired
	OrganizationService orgService;

	private final String identifier = "testOrg";

	@Before
	public void before() {
		orgService.removeOrg(identifier);
	}

	@Test
	public void testCreateOrg() {
		Organization org = orgService.createOrg("test organization",
				identifier, "Hello, this is a test organization!");
		Assert.assertNotNull(org.getId());
		Assert.assertEquals("test organization", org.getName());
		Assert.assertEquals(identifier, org.getIdentifier());
		Assert.assertEquals("Hello, this is a test organization!",
				org.getDescription());

		Organization org1 = orgService.findOrgById(org.getId());
		Assert.assertEquals(org, org1);
		Assert.assertEquals("test organization", org1.getName());
		Assert.assertEquals(identifier, org1.getIdentifier());
		Assert.assertEquals("Hello, this is a test organization!",
				org1.getDescription());

		Organization org2 = orgService.createOrg("test organization",
				identifier, "Hello, this is a test organization!");
		Assert.assertNotNull(org2.getId());
		Assert.assertEquals("test organization", org2.getName());
		Assert.assertNotEquals(identifier, org2.getIdentifier());
		Assert.assertTrue(org2.getIdentifier().startsWith(identifier));
		Assert.assertEquals("Hello, this is a test organization!",
				org2.getDescription());
		orgService.removeOrg(org2);
	}
}
