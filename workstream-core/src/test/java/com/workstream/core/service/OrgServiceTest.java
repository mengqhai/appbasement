package com.workstream.core.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.workstream.core.conf.ApplicationConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfiguration.class)
public class OrgServiceTest {

	@Autowired
	OrganizationService orgService;

	@Test
	public void testCreateOrg() {
		orgService.createOrg("test organization", "testOrg",
				"Hello, this is a test organization!");
	}
}
