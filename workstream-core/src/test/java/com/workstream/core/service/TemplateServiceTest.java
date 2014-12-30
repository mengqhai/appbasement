package com.workstream.core.service;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.workflow.simple.definition.HumanStepDefinition;
import org.activiti.workflow.simple.definition.WorkflowDefinition;
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
		Deployment deploy = temSer.deployFile(org.getId(), fileName,
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

	@Test
	public void testConvertWorkflow() {
		WorkflowDefinition def = new WorkflowDefinition();
		def.setName("Test simple workflow.");
		def.setDescription("This is the simplest workflow");
		def.addHumanStep("HelloTask哦", userId);

		Model model = temSer.saveToModel(org.getId(), def);
		Assert.assertNotNull(model.getId());
		WorkflowDefinition defSaved = temSer.getModelWorkflowDef(model.getId());
		Assert.assertEquals(def.getDescription(), defSaved.getDescription());
		Assert.assertEquals(def.getName(), defSaved.getName());
		Assert.assertEquals(1, defSaved.getSteps().size());
		HumanStepDefinition step1 = (HumanStepDefinition) defSaved.getSteps()
				.get(0);
		Assert.assertEquals("HelloTask哦", step1.getName());
		Assert.assertEquals(userId, step1.getAssignee());

		// test duplicate a model
		Model copy = temSer.duplicateModel(model.getId());
		Assert.assertNotNull(copy.getId());
		Assert.assertTrue(copy.getName().endsWith("_copy"));
		Assert.assertEquals(model.getTenantId(), copy.getTenantId());
		WorkflowDefinition defCopy = temSer.getModelWorkflowDef(model.getId());
		Assert.assertEquals(defSaved.getSteps().size(), defCopy.getSteps()
				.size());
		HumanStepDefinition step1copy = (HumanStepDefinition) defSaved
				.getSteps().get(0);
		Assert.assertEquals(step1.getName(), step1copy.getName());
		Assert.assertEquals(step1.getAssignee(), step1copy.getAssignee());

		Assert.assertTrue(Arrays.equals(temSer.getModelDiagram(model.getId()),
				temSer.getModelDiagram(copy.getId())));

		// test update workflow
		defSaved.addHumanStep("AddedTask", userId);
		temSer.updateModel(org.getId(), model.getId(), defSaved);
		temSer.getModel(model.getId());
		WorkflowDefinition defUpdated = temSer.getModelWorkflowDef(model
				.getId());
		Assert.assertEquals(2, defUpdated.getSteps().size());
		step1 = (HumanStepDefinition) defUpdated.getSteps().get(0);
		Assert.assertEquals("HelloTask哦", step1.getName());
		Assert.assertEquals(userId, step1.getAssignee());
		HumanStepDefinition step2 = (HumanStepDefinition) defUpdated.getSteps()
				.get(1);
		Assert.assertEquals("AddedTask", step2.getName());
		Assert.assertEquals(userId, step2.getAssignee());

		Deployment deploy = temSer.deployModel(model.getId());
		List<Model> modelList = temSer
				.filterModelByDeploymentId(deploy.getId());
		Assert.assertEquals(1, modelList.size());
		Assert.assertEquals(model.getId(), modelList.get(0).getId());

		temSer.removeDeployment(deploy.getId());
		modelList = temSer.filterModelByDeploymentId(deploy.getId());
		Assert.assertEquals(0, modelList.size());
	}
}
