package com.workstream.core.sysprocess;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.User;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.workstream.core.service.CoreFacadeService;
import com.workstream.core.service.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfiguration.class)
@TransactionConfiguration(transactionManager = CoreConstants.TX_MANAGER, defaultRollback = false)
public class UserJoinOrgTest {

	private final static Logger log = LoggerFactory
			.getLogger(UserJoinOrgTest.class);

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

	String userId = "userJoinTester@sina.com";

	String adminId = "admin@other.com";

	String orgIdentifier = "userJoinTestOrg";

	Organization org;

	@Before
	@Transactional(value = CoreConstants.TX_MANAGER, propagation = Propagation.REQUIRED)
	public void before() {
		TestUtils
				.clearUser(userId, core.getUserService(), core.getOrgService());
		TestUtils.clearUser(adminId, core.getUserService(),
				core.getOrgService());
		TestUtils.clearOrg(orgIdentifier, core);
		TestUtils.clearOrphanGroups(idService);
		core.getUserService().createUser(userId, "User to join org", "123");
		core.getUserService().createUser(adminId, "Admin to approval the task",
				"123");
		UserX adminX = core.getUserService().getUserX(adminId);
		org = core.createInitOrg(adminX, "Org to join test", orgIdentifier,
				null);
	}

	@Test
	@Transactional(value = CoreConstants.TX_MANAGER, propagation = Propagation.REQUIRED)
	public void testUserJoinOrgProcessByFacade() {
		// There must be a logged in user
		idService.setAuthenticatedUserId(userId);
		ProcessInstance pi = core.requestUserJoinOrg(org.getId());

		String adminGroupId = core.getOrgAdminGroup(
				core.getOrgService().findOrgByIdentifier(orgIdentifier))
				.getId();
		List<Task> approvalTasks = taskSer.createTaskQuery()
				.processInstanceId(pi.getId()).taskCandidateGroup(adminGroupId)
				.list();
		Assert.assertEquals(1, approvalTasks.size());
		Task approvalTask = approvalTasks.get(0);
		Assert.assertTrue(approvalTask.getName().contains(userId));
		Assert.assertTrue(approvalTask.getName().contains(org.getName()));
		log.info("Task description: {}", approvalTask.getDescription());

		taskSer.claim(approvalTask.getId(), adminId);

		Map<String, Object> props = new HashMap<String, Object>();
		props.put("approval", Boolean.TRUE);
		taskSer.complete(approvalTask.getId(), props);

		List<User> orgUsers = core.getUserService().filterUser(org);
		Assert.assertEquals(2, orgUsers.size());
		boolean userInOrg = false;
		for (User user : orgUsers) {
			if (user.getId().equals(userId)) {
				userInOrg = true;
			}
		}
		Assert.assertTrue(userInOrg);
	}

	@Test
	@Transactional(value = CoreConstants.TX_MANAGER, propagation = Propagation.REQUIRED)
	public void testUserJoinOrgProcess() {
		// repoSer.createDeployment()
		// .addClasspathResource(
		// "com/workstream/core/sysprocess/UserJoinOrg.bpmn")
		// .name("UserJoinOrgDeployment").deploy();

		String adminGroupId = core.getOrgAdminGroup(
				core.getOrgService().findOrgByIdentifier(orgIdentifier))
				.getId();
		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("orgId", org.getId());
		variableMap.put("orgName", org.getName());
		variableMap.put("userId", userId);
		variableMap.put("adminGroupId", adminGroupId);

		// There must be a logged in user
		idService.setAuthenticatedUserId(userId);

		ProcessInstance processInstance = ruSer.startProcessInstanceByKey(
				CoreConstants.PRO_KEY_USER_JOIN_ORG, variableMap);
		assertNotNull(processInstance.getId());
		log.info("process instance id " + processInstance.getId() + " "
				+ processInstance.getProcessDefinitionId());

		List<Task> approvalTasks = taskSer.createTaskQuery()
				.processInstanceId(processInstance.getId())
				.taskCandidateGroup(adminGroupId).list();
		Assert.assertEquals(1, approvalTasks.size());
		Task approvalTask = approvalTasks.get(0);
		Assert.assertTrue(approvalTask.getName().contains(userId));
		Assert.assertTrue(approvalTask.getName().contains(org.getName()));
		log.info("Task description: {}", approvalTask.getDescription());

		taskSer.claim(approvalTask.getId(), adminId);

		Map<String, Object> props = new HashMap<String, Object>();
		props.put("approval", Boolean.TRUE);
		taskSer.complete(approvalTask.getId(), props);

		List<User> orgUsers = core.getUserService().filterUser(org);
		Assert.assertEquals(2, orgUsers.size());
		boolean userInOrg = false;
		for (User user : orgUsers) {
			if (user.getId().equals(userId)) {
				userInOrg = true;
			}
		}
		Assert.assertTrue(userInOrg);
	}
}
