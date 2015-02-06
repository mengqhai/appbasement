package com.workstream.core.service;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.FormService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.form.BooleanFormType;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.activiti.workflow.simple.converter.json.SimpleWorkflowJsonConverter;
import org.activiti.workflow.simple.definition.HumanStepDefinition;
import org.activiti.workflow.simple.definition.WorkflowDefinition;
import org.activiti.workflow.simple.definition.form.BooleanPropertyDefinition;
import org.activiti.workflow.simple.definition.form.FormDefinition;
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
import com.workstream.core.model.Revision;
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
	private ProcessService proSer;

	@Autowired
	private TaskService taskSer;

	String userId = "templateServiceTester@sina.com";

	String orgIdentifier = "templateServiceTestOrg";

	Organization org;

	@Autowired
	private TemplateService temSer;

	@Autowired
	private FormService formSer;

	@Before
	@Transactional(value = CoreConstants.TX_MANAGER, propagation = Propagation.REQUIRES_NEW)
	public void before() {
		TestUtils
				.clearUser(userId, core.getUserService(), core.getOrgService());
		TestUtils.clearOrg(orgIdentifier, core);
		TestUtils.clearOrphanGroups(idService);
		core.getUserService().createUser(userId, "User to join org", "123");
		UserX userX = core.getUserService().getUserX(userId);
		org = core.createInitOrg(userX, "Template Service Test Org",
				orgIdentifier, null);

		// clear old models for the org
		TestUtils.clearModelForOrg(org.getId(), temSer);
		// clear process instances
		TestUtils.clearProcessForOrg(org.getId(), proSer);
		TestUtils.clearOrphanProcesses(ruSer);
		// TestUtils.clearTaskForAssignee(userId, proSer);
		// clear old deployments for the org
		TestUtils.clearDeploymentForOrg(org.getId(), temSer);
	}

	@Test
	public void testCreateDeployment() {
		String fileName = "TestProcess1.bpmn";
		InputStream bpmnIn = this.getClass().getResourceAsStream(
				"/process/" + fileName);
		Deployment deploy = temSer.deployFile(org.getId(), fileName, bpmnIn);
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
		List<ProcessDefinition> pdList1 = temSer.filterProcessTemplate(
				org.getId(), false, 0, 10);
		Assert.assertEquals(1, pdList1.size());

		// test delete
		temSer.removeDeployment(deploy.getId());
		deployList = temSer.filterDeployment(org.getId());
		Assert.assertEquals(0, deployList.size());
		pdList1 = temSer.filterProcessTemplate(org.getId(), false, 0, 10);
		Assert.assertEquals(0, pdList1.size());
	}

	@Test
	public void testCreateModel() {
		Model model = temSer.createModel(org.getId(), "An empty model");
		Model created = temSer.getModel(model.getId());
		Assert.assertNotNull(created);
		Assert.assertEquals(String.valueOf(org.getId()), created.getTenantId());
		Assert.assertEquals("An empty model", created.getName());

		List<Model> models = temSer.filterModel(org.getId(), 0, 10);
		Assert.assertEquals(1, models.size());

		// check revision
		Collection<Revision> revisions = temSer.filterModelRevision(model
				.getId());
		Assert.assertEquals(1, revisions.size());
		Revision rev = revisions.iterator().next();
		Assert.assertEquals(Revision.TYPE_CREATE, rev.getAction());

		temSer.removeModel(model.getId());
		models = temSer.filterModel(org.getId(), 0, 10);
		Assert.assertEquals(0, models.size());

		revisions = temSer.filterModelRevision(model.getId());
		Assert.assertEquals(0, revisions.size());
	}

	@Test
	public void testChoiceWorkflow() {
		WorkflowDefinition def = new WorkflowDefinition();
		def.setName("Process with choice");
		def.addHumanStep("Do you agree?", userId).inChoice().inList()
				.addCondition("agree", "==", "true")
				.addHumanStep("Agreed task", userId).endList().inList()
				.addHumanStep("Not agreed task", userId).endList().endChoice();

		HumanStepDefinition agree = (HumanStepDefinition) def.getSteps().get(0);
		FormDefinition form = new FormDefinition();
		BooleanPropertyDefinition prop1 = new BooleanPropertyDefinition();
		prop1.setType("boolean");
		prop1.setDisplayName("Agree?");
		prop1.setName("agree");
		prop1.setWritable(true);
		prop1.setMandatory(true);
		form.addFormProperty(prop1);
		agree.addForm(form);

		SimpleWorkflowJsonConverter con = new SimpleWorkflowJsonConverter();
		con.writeWorkflowDefinition(def, new PrintWriter(System.out));
		Model model = temSer.saveToModel(org.getId(), def,
				"Process with choice");
		Deployment deploy = temSer.deployModel(model.getId());
		idService.setAuthenticatedUserId(userId);
		ProcessDefinition proDef = temSer.getProcessTemplateByDeployment(deploy
				.getId());
		proSer.startProcess(proDef.getId());

		// check historic process instance
		List<HistoricProcessInstance> hiList = proSer
				.filterHiProcessByOrgStarter(org.getId(), userId, false);
		Assert.assertEquals(1, hiList.size());

		List<Task> tasks = proSer.filterTaskByAssignee(userId, 0, 10);
		Assert.assertEquals(1, tasks.size());
		Task task = tasks.get(0);
		Assert.assertEquals("Do you agree?", task.getName());
		TaskFormData formData = formSer.getTaskFormData(task.getId());
		Assert.assertEquals(1, formData.getFormProperties().size());
		FormProperty formProp = formData.getFormProperties().get(0);
		Assert.assertEquals("agree", formProp.getName());
		Assert.assertTrue(formProp.getType() instanceof BooleanFormType);
		Map<String, String> props = new HashMap<String, String>();
		props.put("agree", "true");
		formSer.submitTaskFormData(task.getId(), props);

		tasks = proSer.filterTaskByAssignee(userId, 0, 10);
		Assert.assertEquals(1, tasks.size());
		task = tasks.get(0);

		Assert.assertEquals("Agreed task", task.getName());
	}

	@Test
	public void testConvertWorkflow() {
		idService.setAuthenticatedUserId(userId); // for revision record
		WorkflowDefinition def = new WorkflowDefinition();
		// def.setName("Test simple workflow."); // no longer saved to json
		def.setDescription("This is the simplest workflow");
		def.addHumanStep("HelloTask哦", userId);

		Model model = temSer.saveToModel(org.getId(), def,
				"Test simple workflow.");
		model.setName("Test simple workflow.");
		Map<String, Object> props = new HashMap<String, Object>();
		props.put("name", "Test simple workflow.");
		temSer.updateModel(model.getId(), props);
		Assert.assertNotNull(model.getId());
		WorkflowDefinition defSaved = temSer.getModelWorkflowDef(model.getId());
		Assert.assertEquals(def.getDescription(), defSaved.getDescription());
		// Assert.assertEquals(def.getName(), defSaved.getName());
		Assert.assertEquals(1, defSaved.getSteps().size());
		HumanStepDefinition step1 = (HumanStepDefinition) defSaved.getSteps()
				.get(0);
		Assert.assertEquals("HelloTask哦", step1.getName());
		Assert.assertEquals(userId, step1.getAssignee());

		// check revision record
		Collection<Revision> revisions = temSer.filterModelRevision(model
				.getId());
		Assert.assertEquals(1, revisions.size());
		Revision rev = revisions.iterator().next();
		Assert.assertEquals(userId, rev.getUserId());
		Assert.assertEquals(Revision.TYPE_CREATE, rev.getAction());

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
		temSer.updateModel(model.getId(), defSaved, "Added an Action 哦");
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

		// check the edit revision record
		revisions = temSer.filterModelRevision(model.getId());
		Assert.assertEquals(2, revisions.size());
		rev = revisions.iterator().next();
		Assert.assertEquals(userId, rev.getUserId());
		Assert.assertEquals(Revision.ACTION_EDIT, rev.getAction());
		Assert.assertEquals("Added an Action 哦", rev.getComment());

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
