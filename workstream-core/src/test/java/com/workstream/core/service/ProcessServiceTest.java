package com.workstream.core.service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.After;
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
public class ProcessServiceTest {

	@Autowired
	private CoreFacadeService core;

	@Autowired
	private IdentityService idService;

	@Autowired
	private ProcessService pSer;

	@Autowired
	private TemplateService tSer;

	String userId = "processServiceTester@sina.com";
	String orgIdentifier = "processServiceTestOrg";
	Organization org;
	Deployment deploy;
	ProcessDefinition pDef;

	@Before
	@Transactional(value = CoreConstants.TX_MANAGER, propagation = Propagation.REQUIRED)
	public void before() {
		TestUtils
				.clearUser(userId, core.getUserService(), core.getOrgService());
		TestUtils.clearOrg(orgIdentifier, core);
		TestUtils.clearOrphanGroups(idService);
		core.getUserService()
				.createUser(userId, "User to start process", "123");
		UserX userX = core.getUserService().getUserX(userId);
		org = core.createInitOrg(userX, "Org to test ProcessService",
				orgIdentifier, null);
		// clear processes for the new org
		TestUtils.clearProcessForOrg(org.getId(), pSer);

		String fileName = "TestProcess1.bpmn";
		InputStream bpmnIn = this.getClass().getResourceAsStream(
				"/process/" + fileName);
		deploy = tSer.deployFile(org.getId(), fileName, bpmnIn);
		idService.setAuthenticatedUserId(userId);
		pDef = tSer.getProcessTemplateByDeployment(deploy.getId());
	}

	@After
	public void after() {
		// undeploy
		tSer.removeDeployment(deploy.getId());
	}

	@Test
	public void testStartProcess() {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("assigneeId", userId);
		ProcessInstance pi = pSer.startProcess(pDef.getId(), vars);
		HistoricProcessInstance hi = pSer.getHiProcess(pi.getId());
		Assert.assertEquals(userId, hi.getStartUserId());
		Assert.assertEquals(String.valueOf(org.getId()), hi.getTenantId());

		// test get HistoricProcessInstance
		List<HistoricProcessInstance> hiList = pSer.filterHiProcessByOrg(
				org.getId(), false);
		Assert.assertEquals(1, hiList.size());
		Assert.assertEquals(hi.getId(), hiList.get(0).getId());

		hiList = pSer.filterHiProcessByOrg(org.getId(), true);
		Assert.assertEquals(0, hiList.size());

		hiList = pSer.filterHiProcessByOrgStarter(org.getId(), userId, false);
		Assert.assertEquals(1, hiList.size());
		Assert.assertEquals(hi.getId(), hiList.get(0).getId());

		List<Task> myTasks = pSer.filterTaskByAssignee(userId);
		Assert.assertEquals(1, myTasks.size());
		Task myTask = myTasks.get(0);
		Assert.assertEquals(pi.getId(), myTask.getProcessInstanceId());
		Assert.assertEquals(pDef.getId(), myTask.getProcessDefinitionId());

		// test get ProcessInstance
		ProcessInstance piLoaded = pSer.getProcess(pi.getId());
		Assert.assertEquals(pi.getId(), piLoaded.getId());
		Assert.assertEquals(String.valueOf(org.getId()), piLoaded.getTenantId());

		// must firstly remove the running instance
		pSer.removeProcess(pi.getId(), "test_delete");
		Assert.assertNull(pSer.getProcess(pi.getId()));
		// the related task must be no longer there
		myTasks = pSer.filterTaskByAssignee(userId);
		Assert.assertEquals(0, myTasks.size());
		// then the historic instance can be removed
		pSer.removeHiProcess(hi.getId());
		Assert.assertEquals(0, pSer.filterHiProcessByStarter(userId, false)
				.size());
	}

	@Test
	public void testCompleteProcess() {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("assigneeId", userId);
		ProcessInstance pi = pSer.startProcess(pDef.getId(), vars);
		List<Task> myTasks = pSer
				.filterTaskByProcess(pi.getProcessInstanceId());
		Assert.assertEquals(1, myTasks.size());
		Task myTask = myTasks.get(0);
		Assert.assertEquals(userId, myTask.getAssignee());
		Assert.assertEquals(pDef.getId(), myTask.getProcessDefinitionId());
		pSer.completeTask(myTask.getId());
		Assert.assertNull(pSer.getProcess(pi.getId()));

		myTasks = pSer.filterTaskByAssignee(userId);
		Assert.assertEquals(0, myTasks.size());
		// then the historic instance can be removed
		pSer.removeHiProcess(pi.getProcessInstanceId());
		// HistoricProcessInstance.id == ProcessInstanceId
		Assert.assertEquals(0, pSer.filterHiProcessByStarter(userId, false)
				.size());
	}

}
