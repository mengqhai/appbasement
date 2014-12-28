package com.workstream.core.service;

import java.io.InputStream;
import java.util.List;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
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
import com.workstream.core.model.Organization;
import com.workstream.core.model.UserX;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfiguration.class)
@TransactionConfiguration(transactionManager = CoreConstants.TX_MANAGER, defaultRollback = false)
public class TemplateServiceTest {

	@Autowired
	RepositoryService repoSer;

	@Autowired
	RuntimeService ruSer;

	@Autowired
	private CoreFacadeService core;

	@Autowired
	private IdentityService idService;

	@Autowired
	private TaskService taskSer;

	String userId = "templateServiceTester@sina.com";

	String orgIdentifier = "templateServiceTestOrg";

	Organization org;

	@Autowired
	private TemplateService temSer;

	@Before
	@Transactional(value = CoreConstants.TX_MANAGER, propagation = Propagation.REQUIRED)
	public void before() {
		TestUtils
				.clearUser(userId, core.getUserService(), core.getOrgService());
		TestUtils.clearOrg(orgIdentifier, core);
		TestUtils.clearOrphanGroups(idService);
		core.getUserService().createUser(userId, "User to join org", "123");
		UserX userX = core.getUserService().getUserX(userId);
		org = core.createInitOrg(userX, "Template Service Test Org",
				orgIdentifier, null);
	}

	@Test
	public void testCreateDeployment() {
		String fileName = "TestProcess1.bpmn";
		InputStream bpmnIn = this.getClass().getResourceAsStream(
				"/process/" + fileName);
		Deployment deploy = temSer.createDeployment(org.getId(), fileName,
				bpmnIn);
		Assert.assertNotNull(deploy.getId());
		Assert.assertEquals(String.valueOf(org.getId()), deploy.getTenantId());

		List<Deployment> deployList = temSer.filterDeployment(org.getId());
		Assert.assertEquals(1, deployList.size());
		Assert.assertEquals(deploy.getId(), deployList.get(0).getId());
		Assert.assertEquals(String.valueOf(org.getId()), deployList.get(0)
				.getTenantId());

		List<ProcessDefinition> pdList = temSer.filterProcessTemplate(deploy
				.getId());
		Assert.assertEquals(1, pdList.size());
		List<ProcessDefinition> pdList1 = temSer.filterProcessTemplate(org
				.getId());
		Assert.assertEquals(1, pdList1.size());

		// test delete
		temSer.removeDeployment(deploy.getId());
		deployList = temSer.filterDeployment(org.getId());
		Assert.assertEquals(0, deployList.size());
		pdList1 = temSer.filterProcessTemplate(org.getId());
		Assert.assertEquals(0, pdList1.size());
	}

	@Test
	public void testCreateModel() {
		Model model = temSer.createModel(org.getId(), "An empty model");
		Model created = temSer.getModel(model.getId());
		Assert.assertNotNull(created);
		Assert.assertEquals(String.valueOf(org.getId()), created.getTenantId());
		Assert.assertEquals("An empty model", created.getName());

		List<Model> models = temSer.filterModel(org.getId());
		Assert.assertEquals(1, models.size());

		temSer.removeModel(model.getId());
		models = temSer.filterModel(org.getId());
		Assert.assertEquals(0, models.size());
	}
}