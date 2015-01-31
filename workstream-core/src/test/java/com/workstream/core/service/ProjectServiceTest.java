package com.workstream.core.service;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.IdentityService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Event;
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
		TestUtils.clearOrphanTasks(taskSer);
		core.getUserService().removeUser(userId);
		core.getUserService().createUser(userId, "Project tester user", "123");
		userX = core.getUserService().getUserX(userId);
		org = core
				.createInitOrg(userX, "Project Test Org", orgIdentifier, null);
	}

	@Test
	public void testCreateProject() {
		Assert.assertNotNull(CoreFacadeService.getInstance());

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

	@Test
	public void testCreateSubTask() {
		Project pro = proSer.createProject(org, "Project test subtask");
		Task parent = proSer.createTask(pro.getId(), userId, "Task #1", null,
				null, null, null);
		Task sub = proSer.createTask(pro, userId, "Task #1", null, null, null,
				null, parent.getId());

		Task subSaved = proSer.getTask(sub.getId());
		Assert.assertEquals(parent.getId(), subSaved.getParentTaskId());

		proSer.deleteTask(parent);
		subSaved = proSer.getTask(sub.getId());
		// if the parent task is deleted sub task will also be deleted
		Assert.assertNull(subSaved);
	}

	@Transactional(value = CoreConstants.TX_MANAGER, propagation = Propagation.REQUIRES_NEW)
	@Test
	public void testCreateTask() {
		Project pro = proSer.createProject(org, "Project #2");
		Assert.assertEquals(0, proSer.filterTask(pro, 0, 10).size());

		Task task = proSer.createTask(pro.getId(), userId, "Task #1", null,
				null, null, null);
		Task created = proSer.getTask(task.getId());
		Assert.assertEquals("Task #1", created.getName());
		Assert.assertEquals(String.valueOf(pro.getOrg().getId()),
				task.getTenantId());
		List<Task> taskList = proSer.filterTask(pro, 0, 10);
		Assert.assertEquals(1, taskList.size());
		Assert.assertEquals(created.getId(), taskList.get(0).getId());

		// there should be events for this created task
		List<Event> events = proSer.filterTaskEvent(task.getId());
		Assert.assertEquals(1, events.size());
		Assert.assertEquals(userId + "_|_owner", events.get(0).getMessage());

		// test task deletion
		task = proSer.createTask(pro.getId(), userId, "Task #2", null, null,
				null, null);
		taskList = proSer.filterTask(pro, 0, 10);
		Assert.assertEquals(2, taskList.size());
		proSer.deleteTask(task);
		taskList = proSer.filterTask(pro, 0, 10);
		Assert.assertEquals(1, taskList.size());
		Assert.assertEquals(created.getId(), taskList.get(0).getId());

		// delete the project
		proSer.deleteProject(pro);
		taskList = proSer.filterTask(pro, 0, 10);

		Long orgId = pro.getOrg().getId();
		Long proId = pro.getId();

		TaskQuery q = taskSer.createTaskQuery()
				.taskTenantId(String.valueOf(orgId))
				.taskCategory(String.valueOf(proId));
		Assert.assertEquals(0, q.count());
	}

	@Test
	public void testAssignTask() {
		Project pro = proSer.createProject(org, "Project #3");
		Project pro1 = proSer.createProject(org, "Project #4");
		Task task = proSer.createTask(pro.getId(), userId, "Task #3.1", null,
				null, null, null);
		Task task1 = proSer.createTask(pro1.getId(), userId, "Task #4.1", null,
				null, userId, null);
		Map<String, String> props = new HashMap<String, String>();
		props.put("assignee", userX.getUserId()); // must check if an assignee
													// event is created
		proSer.updateTask(task.getId(), props);

		List<Task> taskList = proSer.filterTask(pro, userId, 0, 10);
		Assert.assertEquals(1, taskList.size());
		Assert.assertEquals(task.getId(), taskList.get(0).getId());
		Assert.assertEquals(userId, taskList.get(0).getAssignee());

		// there should be events for this created task
		List<Event> events = proSer.filterTaskEvent(task.getId());
		Assert.assertEquals(2, events.size());
		Set<String> messages = new HashSet<String>();
		messages.add(events.get(0).getMessage());
		messages.add(events.get(1).getMessage());
		Assert.assertTrue(messages.contains(userId + "_|_owner"));
		Assert.assertTrue(messages.contains(userId + "_|_assignee"));

		// there should be events for this created task
		events = proSer.filterTaskEvent(task1.getId());
		Assert.assertEquals(2, events.size());
		messages = new HashSet<String>();
		messages.add(events.get(0).getMessage());
		messages.add(events.get(1).getMessage());
		Assert.assertTrue(messages.contains(userId + "_|_owner"));
		Assert.assertTrue(messages.contains(userId + "_|_assignee"));

		taskList = proSer.filterTaskByAssignee(userId, 0, 10); // only by user,
																// doesn't
		// care about project
		Assert.assertEquals(2, taskList.size());
		for (Task t : taskList) {
			Assert.assertEquals(userId, t.getAssignee());
		}

		taskList = proSer.filterTaskByCreator(userId, 0, 10);
		Assert.assertEquals(2, taskList.size());
		for (Task t : taskList) {
			Assert.assertEquals(userId, t.getOwner());
		}

		// unassign task1
		props = new HashMap<String, String>();
		props.put("assignee", null);
		proSer.updateTask(task1.getId(), props);
		task1 = proSer.getTask(task1.getId());
		Assert.assertNull(task1.getAssignee());
		// one more unset assignee event should be in db
		events = proSer.filterTaskEvent(task1.getId());
		Assert.assertEquals(3, events.size());
		Event latestEvent = events.get(0);
		latestEvent.getAction().equals(Event.ACTION_DELETE_USER_LINK);
		latestEvent.getMessage().equals("null_|_assignee");

	}

	@Test
	public void testCommentTask() throws Exception {
		Project pro = proSer.createProject(org, "Project comment task");
		Task task = proSer.createTask(pro.getId(), userId,
				"Task with comments", null, null, null, null);
		// AddCommentCmd uses Authentication.getAuthenticatedUserId(); to decide
		// the current user
		core.getUserService().login(userId);
		Comment com = proSer.addTaskComment(task.getId(), "Comment 1");
		Thread.sleep(100L); // makes the time stamp definitely different
		Comment com1 = proSer.addTaskComment(task.getId(), "Comment 2");
		Set<String> comments = new HashSet<String>();
		comments.add(com.getFullMessage());
		comments.add(com1.getFullMessage());

		List<Comment> commentList = proSer.filterTaskComment(task.getId());
		Assert.assertEquals(2, commentList.size());
		for (Comment comment : commentList) {
			Assert.assertTrue(comments.contains(comment.getFullMessage()));
		}

		List<Event> events = proSer.filterTaskEvent(task.getId());
		Assert.assertEquals(3, events.size()); // one owner event and 2 comment
												// events
		Event latestEvent = events.get(0);
		Assert.assertEquals("AddComment", latestEvent.getAction());
		Assert.assertEquals("Comment 2", latestEvent.getMessage());

		proSer.deleteTask(task); // this makes the task archived
		Assert.assertEquals(2, proSer.filterTaskComment(task.getId()).size()); // so
		// the comments must still be there.
		events = proSer.filterTaskEvent(task.getId());
		Assert.assertEquals(3, events.size()); // so does the events
	}

	@Test
	public void testUpdateTaskWithEvent() {
		idService.setAuthenticatedUserId(userId);
		Project pro = proSer.createProject(org, "Project test task with event");
		Task task = proSer.createTask(pro.getId(), userId,
				"Task with update events", null, null, null, null);
		Map<String, Object> props = new HashMap<String, Object>();
		props.put("dueDate", new Date());
		props.put("name", "Updated Task Name");
		props.put("description", "Updated Task Description");
		proSer.updateTask(task.getId(), props);

		List<Event> events = proSer.filterTaskEvent(task.getId());
		Assert.assertEquals(4, events.size());
	}
}
