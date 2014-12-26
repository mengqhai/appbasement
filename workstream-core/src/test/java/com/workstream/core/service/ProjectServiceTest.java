package com.workstream.core.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
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
import com.workstream.core.model.Project;
import com.workstream.core.model.UserX;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfiguration.class)
@TransactionConfiguration(transactionManager = CoreConstants.TX_MANAGER, defaultRollback = false)
public class ProjectServiceTest {

	@Autowired
	private ProjectService proSer;

	@Autowired
	private IdentityService idService;

	@Autowired
	private CoreFacadeService core;

	@Autowired
	private TaskService taskSer;

	String userId = "projectTester@sina.com";

	String orgIdentifier = "projectTestOrg";

	private Organization org;

	private UserX userX;

	@Before
	@Transactional(value = CoreConstants.TX_MANAGER, propagation = Propagation.REQUIRED)
	public void before() throws Exception {
		TestUtils
				.clearUser(userId, core.getUserService(), core.getOrgService());
		TestUtils.clearOrg(orgIdentifier, core);
		TestUtils.clearOrphanGroups(idService);
		core.getUserService().removeUser(userId);
		core.getUserService().createUser(userId, "Project tester user", "123");
		userX = core.getUserService().getUserX(userId);
		org = core
				.createInitOrg(userX, "Project Test Org", orgIdentifier, null);
	}

	@Test
	public void testCreateProject() {
		Project pro = proSer.createProject(org, "Project #1");
		Project proCreated = proSer.getProject(pro.getId());
		Assert.assertEquals(pro.getId(), proCreated.getId());
		Assert.assertEquals("Project #1", proCreated.getName());

		Map<String, Object> props = new HashMap<String, Object>();
		long now = System.currentTimeMillis();
		props.put("startTime", new Date(now));
		props.put("dueTime", new Date(now + 30000L));
		props.put("description", "something to say...");
		proSer.updateProject(pro.getId(), props);

		Project updated = proSer.getProject(pro.getId());
		Assert.assertEquals(now, updated.getStartTime().getTime());
		Assert.assertEquals(now + 30000L, updated.getDueTime().getTime());
		Assert.assertEquals("something to say...", updated.getDescription());

		proSer.deleteProject(pro);
		Project proDeleted = proSer.getProject(pro.getId());
		Assert.assertNull(proDeleted);
	}

	@Transactional(value = CoreConstants.TX_MANAGER, propagation = Propagation.REQUIRES_NEW)
	@Test
	public void testCreateTask() {
		Project pro = proSer.createProject(org, "Project #2");
		Assert.assertEquals(0, proSer.filterTask(pro).size());

		Task task = proSer.createTask(pro.getId(), "Task #1", null, null, null,
				null);
		Task created = proSer.getTask(task.getId());
		Assert.assertEquals("Task #1", created.getName());
		Assert.assertEquals(String.valueOf(pro.getOrg().getId()),
				task.getTenantId());
		List<Task> taskList = proSer.filterTask(pro);
		Assert.assertEquals(1, taskList.size());
		Assert.assertEquals(created.getId(), taskList.get(0).getId());

		// test task deletion
		task = proSer
				.createTask(pro.getId(), "Task #2", null, null, null, null);
		taskList = proSer.filterTask(pro);
		Assert.assertEquals(2, taskList.size());
		proSer.deleteTask(task);
		taskList = proSer.filterTask(pro);
		Assert.assertEquals(1, taskList.size());
		Assert.assertEquals(created.getId(), taskList.get(0).getId());

		// delete the project
		proSer.deleteProject(pro);
		taskList = proSer.filterTask(pro);

		Long orgId = pro.getOrg().getId();
		Long proId = pro.getId();

		TaskQuery q = taskSer.createTaskQuery()
				.taskTenantId(String.valueOf(orgId))
				.taskCategory(String.valueOf(proId));
		Assert.assertEquals(0, q.count());
	}

}
