package com.angularjsplay.e2e.rest;

import static com.angularjsplay.persistence.util.ScrumTestConstants.URL_BASE_COMMON;
import static junitparams.JUnitParamsRunner.$;

import java.sql.Timestamp;
import java.util.Date;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

import com.angularjsplay.e2e.util.RestTestUtils;
import com.angularjsplay.model.Task;
import com.angularjsplay.model.Task.TaskState;
import com.angularjsplay.persistence.util.ScrumTestConstants;
import com.appbasement.component.IObjectPatcher;
import com.appbasement.component.ObjectPatcher;
import com.appbasement.persistence.util.DBUnitHelper;
import com.appbasement.persistence.util.EmfHelper;

@RunWith(JUnitParamsRunner.class)
public class TaskRestTest {

	RestTemplate rest;

	public final static String URL_BASE = URL_BASE_COMMON + "/tasks/";

	@BeforeClass
	public static void setUpBeforeClass() {
		EmfHelper.initEmf();
	}

	@Before
	public void setUp() {
		DBUnitHelper.cleanAll(EmfHelper.getEmf());
		DBUnitHelper.importSmallDataSet(EmfHelper.getEmf());
		DBUnitHelper.importDataSet(EmfHelper.getEmf(),
				ScrumTestConstants.DATA_SET_SMALL_PROJECT,
				ScrumTestConstants.DATA_SET_SMALL_SPRINT,
				ScrumTestConstants.DATA_SET_SMALL_BACKLOG,
				ScrumTestConstants.DATA_SET_SMALL_TASK);
		rest = new RestTemplate();
	}

	@AfterClass
	public static void tearDownAfterClass() {
		EmfHelper.closeEmf();
	}

	public TaskRestTest() {
	}

	@Test
	public void testGetTasks() {
		Task[] tasks = rest.getForObject(URL_BASE, Task[].class);
		Assert.assertEquals(5, tasks.length);
		for (Task task : tasks) {
			Assert.assertNotNull(task.getName());
			Assert.assertNotNull(task.getId());
			Assert.assertNotNull(task.getState());
		}
	}

	public Object[] getTaskParams() {
		Task t1 = new Task();
		t1.setId(5l);
		t1.setName("eu sem. Pellentesque ut ipsum ac");
		t1.setDesc("faucibus ut, nulla. Cras eu tellus eu augue porttitor interdum. Sed auctor odio a purus. Duis elementum, dui quis accumsan convallis, ante lectus convallis est, vitae sodales nisi magna sed");
		t1.setEstimation((short) 95);
		t1.setRemaining((short) 4);
		t1.setState(TaskState.CANCELED);
		t1.setCreatedAt(Timestamp.valueOf("2012-08-22 09:59:36"));

		Task t2 = new Task();
		t2.setId(1l);
		t2.setName("mauris id sapien. Cras dolor dolor,");
		t2.setDesc("consectetuer adipiscing elit. Etiam laoreet, libero et tristique pellentesque, tellus sem mollis dui, in sodales elit erat vitae risus. Duis a mi fringilla mi lacinia mattis. Integer eu lacus. Quisque imperdiet, erat nonummy ultricies ornare,");
		t2.setEstimation((short) 57);
		t2.setRemaining((short) 49);
		t2.setState(TaskState.FINISHED);
		t2.setCreatedAt(Timestamp.valueOf("2012-10-05 15:15:14"));
		t2.setOwnerId(1l);
		t2.setBacklogId(1l);

		return $($(t1), $(t2));
	}

	@Parameters(method = "getTaskParams")
	@Test
	public void testGetTask(Task expected) {
		String url = URL_BASE + expected.getId();
		Task actual = rest.getForObject(url, Task.class);
		RestTestUtils.assertTaskEqual(expected, actual);
	}

	@Test
	public void testGetTaskInvalid() {
		RestTestUtils.assertRestError(rest, HttpMethod.GET, URL_BASE + "3255",
				null, HttpStatus.NOT_FOUND);
	}

	public Object[] getTaskToCreate() {
		Task t1 = new Task();
		t1.setName("A new task");

		Task t2 = new Task();
		t2.setName("A task with desc");
		t2.setDesc("Hello desc");

		Task t3 = new Task();
		t3.setName("A task with backlog");
		t3.setBacklogId(1l);

		Task t4 = new Task();
		t4.setName("A task with owner");
		t4.setOwnerId(1l);

		return $($(t1), $(t2), $(t3), $(t4));
	}

	@Parameters(method = "getTaskToCreate")
	@Test
	public void testCreateTask(Task toCreate) {
		Date now = new Date();
		Task created = rest.postForObject(URL_BASE, toCreate, Task.class);
		Assert.assertNotNull(created.getId());
		Assert.assertTrue(Math.abs(created.getCreatedAt().getTime()
				- now.getTime()) < 30000);
		toCreate.setCreatedAt(created.getCreatedAt());
		toCreate.setId(created.getId());
		RestTestUtils.assertTaskEqual(toCreate, created);
	}

	public Object[] getTaskToCreateInvalid() {
		// null name
		Task t1 = new Task();

		Task t2 = new Task();
		t2.setName("");

		Task t3 = new Task();
		StringBuilder bigName = new StringBuilder();
		for (int i = 0; i < 280; i++) {
			bigName.append("0");
		}
		t3.setName(bigName.toString());

		Task t4 = new Task();
		t4.setName("Negative estimation");
		t4.setEstimation((short) -1);

		Task t5 = new Task();
		t5.setName("Negative remaining");
		t5.setRemaining((short) -1);

		Task t6 = new Task();
		t6.setName("No such owner");
		t6.setOwnerId(9999l);

		Task t7 = new Task();
		t7.setName("No such backlog");
		t7.setBacklogId(9999l);

		Task t8 = new Task();
		t8.setName("Remaining bigger than estimation");
		t8.setRemaining((short) 99);
		t8.setEstimation((short) 5);

		return $($(t1, HttpStatus.BAD_REQUEST), $(t2, HttpStatus.BAD_REQUEST),
				$(t3, HttpStatus.BAD_REQUEST), $(t4, HttpStatus.BAD_REQUEST),
				$(t5, HttpStatus.BAD_REQUEST), $(t6, HttpStatus.NOT_FOUND),
				$(t7, HttpStatus.NOT_FOUND), $(t8, HttpStatus.BAD_REQUEST));

	}

	@Parameters(method = "getTaskToCreateInvalid")
	@Test
	public void testCreateTaskInvalid(Task toCreate, HttpStatus status) {
		RestTestUtils.assertRestError(rest, HttpMethod.POST, URL_BASE,
				toCreate, status);
	}

	@Test
	public void testDeleteTask() {
		String url = URL_BASE + "1";
		rest.delete(url);
		RestTestUtils.assertRestError(rest, HttpMethod.GET, url, null,
				HttpStatus.NOT_FOUND);
	}

	@Test
	public void testDeleteTaskInvalid() {
		String url = URL_BASE + "99999";
		RestTestUtils.assertRestError(rest, HttpMethod.DELETE, url, null,
				HttpStatus.NOT_FOUND);
	}

	public Object[] getTaskForUpdate() {
		Task t1 = new Task();
		t1.setName("New name");

		Task t2 = new Task();
		t2.setDesc("New desc");

		Task t3 = new Task();
		t3.setEstimation((short) 99);

		Task t4 = new Task();
		t4.setRemaining((short) 35);

		Task t5 = new Task();
		t5.setState(TaskState.IN_PROGRESS);

		Task t6 = new Task();
		t6.setBacklogId(3l);

		Task t7 = new Task();
		t7.setOwnerId(2l);

		return $($(t1), $(t2), $(t3), $(t4), $(t5), $(t6), $(t7));
	}

	@Parameters(method = "getTaskForUpdate")
	@Test
	public void testUpdateTask(Task patch) {
		String url = URL_BASE + "1";
		rest.put(url, patch);
		Task updated = rest.getForObject(url, Task.class);
		IObjectPatcher patcher = new ObjectPatcher();
		Assert.assertTrue(patcher.patchObject(updated, patch).isEmpty());
	}

	public Object[] getTaskForUpdateInvalid() {
		Task t2 = new Task();
		t2.setName("");

		Task t3 = new Task();
		StringBuilder bigName = new StringBuilder();
		for (int i = 0; i < 280; i++) {
			bigName.append("0");
		}
		t3.setName(bigName.toString());

		Task t4 = new Task();
		t4.setEstimation((short) -1);

		Task t5 = new Task();
		t5.setRemaining((short) -1);

		Task t6 = new Task();
		t6.setOwnerId(9999l);

		Task t7 = new Task();
		t7.setBacklogId(9999l);

		Task t8 = new Task();
		t8.setName("Remaining bigger than estimation");
		t8.setRemaining((short) 99);
		t8.setEstimation((short) 5);

		Task t9 = new Task();
		t9.setEstimation((short) 5);

		Task t10 = new Task();
		t10.setRemaining((short) 999);

		return $($(t2, HttpStatus.BAD_REQUEST), $(t3, HttpStatus.BAD_REQUEST),
				$(t4, HttpStatus.BAD_REQUEST), $(t5, HttpStatus.BAD_REQUEST),
				$(t6, HttpStatus.NOT_FOUND), $(t7, HttpStatus.NOT_FOUND),
				$(t8, HttpStatus.BAD_REQUEST), $(t9, HttpStatus.BAD_REQUEST),
				$(t10, HttpStatus.BAD_REQUEST));
	}

	@Parameters(method = "getTaskForUpdateInvalid")
	@Test
	public void testUpdateTaskInvalid(Task patch, HttpStatus status) {
		String url = URL_BASE + "1";
		Task beforeUpdate = rest.getForObject(url, Task.class);

		RestTestUtils.assertRestError(rest, HttpMethod.PUT, url, patch, status);

		// Make sure invalid update is rolled back
		Task t = rest.getForObject(url, Task.class);
		IObjectPatcher patcher = new ObjectPatcher();
		Assert.assertTrue(patcher.patchObject(t, beforeUpdate).isEmpty());
	}

}
