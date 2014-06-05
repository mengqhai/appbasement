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

import com.angularjsplay.e2e.util.BasicAuthRestTemplate;
import com.angularjsplay.e2e.util.RestTestUtils;
import com.angularjsplay.model.Backlog;
import com.angularjsplay.persistence.util.ScrumTestConstants;
import com.appbasement.component.IObjectPatcher;
import com.appbasement.component.ObjectPatcher;
import com.appbasement.persistence.util.DBUnitHelper;
import com.appbasement.persistence.util.EmfHelper;

@RunWith(JUnitParamsRunner.class)
public class BacklogRestTest {
	RestTemplate rest;

	public final static String URL_BASE = URL_BASE_COMMON + "/backlogs/";

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
		// rest = new RestTemplate();
		rest = new BasicAuthRestTemplate();
		((BasicAuthRestTemplate) rest).setUsername("mqhnow1");
		((BasicAuthRestTemplate) rest).setPassword("passw0rd");
	}

	@AfterClass
	public static void tearDownAfterClass() {
		EmfHelper.closeEmf();
	}

	public BacklogRestTest() {
	}

	@Test
	public void testGetBacklogs() {
		Backlog[] backlogs = rest.getForObject(URL_BASE, Backlog[].class);
		Assert.assertEquals(20, backlogs.length);
		for (Backlog b : backlogs) {
			Assert.assertNotNull(b.getId());
			Assert.assertNotNull(b.getName());
			Assert.assertNotNull(b.getCreatedAt());
		}

		Long backlogCount = rest.getForObject(URL_BASE + "?count", Long.class);
		Assert.assertEquals(Long.valueOf(backlogs.length), backlogCount);

		int first = 1, max = 3;
		Backlog[] backlogs0 = rest.getForObject(URL_BASE + "?first=" + first
				+ "&max=" + max, Backlog[].class);
		Assert.assertEquals(max, backlogs0.length);
		Assert.assertEquals(backlogs[first], backlogs0[0]);
		Assert.assertEquals(backlogs[first].getId(), backlogs0[0].getId());
	}

	public Object[] getBacklogParams() {
		Backlog b1 = new Backlog();
		b1.setId(20l);
		b1.setName("scelerisque, lorem ipsum sodales purus, in molestie tortor nibh sit");
		b1.setDesc("a, malesuada id, erat. Etiam vestibulum massa rutrum magna. Cras conv"
				+ "allis convallis dolor. Quisque tincidunt pede ac urna. Ut tincidunt vehicula risus. Nulla eget metus eu");
		b1.setPriority((short) 8);
		b1.setEstimation((short) 625);
		b1.setProjectId(2l);
		b1.setCreatedAt(Timestamp.valueOf("2012-05-19 04:46:11"));

		Backlog b2 = new Backlog();
		b2.setId(15l);
		b2.setName("Aliquam tincidunt, nunc ac mattis ornare, lectus ante dictum mi,");
		b2.setDesc("sit amet ante. Vivamus non lorem vitae odio sagittis semper. Nam temp"
				+ "or diam dictum sapien. Aenean massa. Integer vitae nibh. Donec est mauris, rhoncus id,");
		b2.setPriority((short) 10);
		b2.setEstimation((short) 298);
		b2.setCreatedAt(Timestamp.valueOf("2010-10-16 09:59:57"));
		b2.setProjectId(2l);
		b2.setSprintId(1l);
		return $($(b1), $(b2));
	}

	@Parameters(method = "getBacklogParams")
	@Test
	public void testGetBacklog(Backlog expected) {
		Backlog backlog = rest.getForObject(URL_BASE + expected.getId(),
				Backlog.class);
		RestTestUtils.assertBacklogsEqual(expected, backlog);
	}

	@Test
	public void testGetBacklogInvalid() {
		RestTestUtils.assertRestError(rest, HttpMethod.GET, URL_BASE + "3255",
				null, HttpStatus.NOT_FOUND);
	}

	public Object[] getBacklogToCreate() {
		Backlog b1 = new Backlog();
		b1.setName("A backlog to create");
		b1.setDesc("I should be created successfully!");
		b1.setPriority((short) 9);
		b1.setEstimation((short) 35);
		b1.setProjectId(1l);
		b1.setSprintId(8l);

		// no optional properties
		Backlog b2 = new Backlog();
		b2.setName("A backlog to create");
		b2.setProjectId(2l);

		return $($(b1), $(b2));
	}

	@Parameters(method = "getBacklogToCreate")
	@Test
	public void testCreateBacklog(Backlog toCreate) {
		Date now = new Date();
		Backlog created = rest.postForObject(URL_BASE, toCreate, Backlog.class);
		Assert.assertNotNull(created.getId());
		Assert.assertNotNull(created.getCreatedAt());
		Assert.assertTrue(Math.abs(created.getCreatedAt().getTime()
				- now.getTime()) < 30000);
		toCreate.setId(created.getId());
		toCreate.setCreatedAt(created.getCreatedAt());
		RestTestUtils.assertBacklogsEqual(toCreate, created);
	}

	public Object[] getBacklogToCreateInvalid() {
		// null name
		Backlog b1 = new Backlog();
		b1.setProjectId(1l);

		// empty name
		Backlog b2 = new Backlog();
		b2.setName("");
		b2.setProjectId(1l);

		// null project
		Backlog b3 = new Backlog();
		b3.setName("a name");

		Backlog b4 = new Backlog();
		StringBuilder bigName = new StringBuilder();
		for (int i = 0; i < 280; i++) {
			bigName.append("0");
		}
		b4.setName(bigName.toString());
		b4.setProjectId(1l);

		Backlog b5 = new Backlog();
		b5.setName("No such project");
		b5.setProjectId(9999l);

		Backlog b6 = new Backlog();
		b6.setName("No such sprint");
		b6.setProjectId(1l);
		b6.setSprintId(0l);

		Backlog b7 = new Backlog();
		b7.setName("New sprint not in new project");
		b7.setProjectId(2l);
		b7.setSprintId(3l);

		Backlog b8 = new Backlog();
		b8.setName("Priority too big");
		b8.setProjectId(1l);
		b8.setPriority((short) 15);

		Backlog b9 = new Backlog();
		b9.setProjectId(1l);
		b9.setName("Priority too small");
		b9.setPriority((short) 0);

		Backlog b10 = new Backlog();
		b10.setProjectId(1l);
		b10.setName("Invalid estimation");
		b10.setEstimation((short) 0);

		Backlog b11 = new Backlog();
		b11.setProjectId(1l);
		b11.setName("Negative estimation");
		b11.setEstimation((short) -1);

		Backlog b12 = new Backlog();
		b12.setName("Sprint not in the project #1");
		b12.setProjectId(1l);
		b12.setSprintId(1l);
		return $($(b1, HttpStatus.BAD_REQUEST), $(b2, HttpStatus.BAD_REQUEST),
				$(b3, HttpStatus.BAD_REQUEST), $(b4, HttpStatus.BAD_REQUEST),
				$(b5, HttpStatus.NOT_FOUND), $(b6, HttpStatus.NOT_FOUND),
				$(b7, HttpStatus.BAD_REQUEST), $(b8, HttpStatus.BAD_REQUEST),
				$(b9, HttpStatus.BAD_REQUEST), $(b10, HttpStatus.BAD_REQUEST),
				$(b11, HttpStatus.BAD_REQUEST), $(b12, HttpStatus.BAD_REQUEST));
	}

	@Parameters(method = "getBacklogToCreateInvalid")
	@Test
	public void testCreateBaclogInvalid(Backlog toCreate, HttpStatus status) {
		RestTestUtils.assertRestError(rest, HttpMethod.POST, URL_BASE,
				toCreate, status);
	}

	@Test
	public void testDeleteBacklog() {
		String url = URL_BASE + "1";
		rest.delete(url);
		RestTestUtils.assertRestError(rest, HttpMethod.GET, url, null,
				HttpStatus.NOT_FOUND);
	}

	@Test
	public void testDeleteBacklogInvalid() {
		String url = URL_BASE + "99999";
		RestTestUtils.assertRestError(rest, HttpMethod.DELETE, url, null,
				HttpStatus.NOT_FOUND);
	}

	public Object[] getBacklogForUpdate() {
		// limitation of spring RestTemplate
		// if not explicitly set sprintId,
		// "sprintId":null
		// will always in the json request body
		// witch results in backlog.removeSprint==true
		Backlog b1 = new Backlog();
		b1.setName("Name changed");

		Backlog b2 = new Backlog();
		b2.setProjectId(1l);

		Backlog b3 = new Backlog();
		b3.setSprintId(5l);

		Backlog b4 = new Backlog();
		b4.setProjectId(1l);
		b4.setSprintId(4l);

		Backlog b5 = new Backlog();
		b5.setSprintId(null); // remove sprint
		return $($(b1), $(b2), $(b3), $(b4), $(b5));
	}

	@Parameters(method = "getBacklogForUpdate")
	@Test
	public void testUpdateBacklog(Backlog patch) {
		// limitation of spring RestTemplate
		// if not explicitly set sprintId,
		// "sprintId":null
		// will always in the json request body
		// witch results in backlog.removeSprint==true
		String url = URL_BASE + "1";
		if (!patch.isRemoveSprint()) {
			Backlog beforeUpdate = rest.getForObject(url, Backlog.class);
			Long sprintId = beforeUpdate.getSprintId();
			patch.setSprintId(sprintId);
		}

		rest.put(url, patch);
		Backlog updated = rest.getForObject(url, Backlog.class);
		IObjectPatcher patcher = new ObjectPatcher();
		Assert.assertTrue(patcher.patchObject(updated, patch).isEmpty());

		if (patch.isRemoveSprint()) {
			Assert.assertNull(updated.getSprintId());
		} else {
			Assert.assertNotNull(updated.getSprintId());
		}

	}

	@Test
	public void testUpdateBacklogUnsetSprint() {
		String url = URL_BASE + "1";
		Backlog patch = new Backlog();
		patch.setSprintId(null); // explicitly unset sprint
		rest.put(url, patch);
		Backlog updated = rest.getForObject(url, Backlog.class);
		Assert.assertNull(updated.getSprintId());
		Assert.assertNull(updated.getSprint());
	}

	public Object[] getBacklogForUpdateInvalid() {
		// limitation of spring RestTemplate
		// if not explicitly set sprintId,
		// "sprintId":null
		// will always in the json request body
		// witch results in backlog.removeSprint==true
		Backlog b1 = new Backlog();
		b1.setName("");
		b1.setSprintId(3l);

		Backlog b2 = new Backlog();
		b2.setName("Invalid estimation");
		b2.setEstimation((short) 0);
		b2.setSprintId(3l);

		Backlog b3 = new Backlog();
		b3.setName("Negative estimation");
		b3.setEstimation((short) -1);
		b3.setSprintId(3l);

		Backlog b4 = new Backlog();
		b4.setName("Sprint not in the project #1");
		// b4.setProjectId(1l); backlog #1 already in project #1
		b4.setSprintId(1l);

		Backlog b5 = new Backlog();
		b5.setName("No such project");
		b5.setProjectId(9999l);
		b5.setSprintId(3l);

		Backlog b6 = new Backlog();
		b6.setName("No such sprint");
		b6.setProjectId(1l);
		b6.setSprintId(0l);

		Backlog b7 = new Backlog();
		b7.setName("New sprint not in new project");
		b7.setProjectId(2l);
		b7.setSprintId(3l);

		Backlog b71 = new Backlog();
		b71.setName("Old sprint not in new project");
		b71.setProjectId(2l);
		b71.setSprintId(3l);

		Backlog b8 = new Backlog();
		b8.setName("Priority too big");
		b8.setPriority((short) 15);
		b8.setSprintId(3l);

		Backlog b9 = new Backlog();
		b9.setName("Priority too small");
		b9.setPriority((short) 0);
		b9.setSprintId(3l);

		return $($(b1, HttpStatus.BAD_REQUEST), $(b2, HttpStatus.BAD_REQUEST),
				$(b3, HttpStatus.BAD_REQUEST), $(b4, HttpStatus.BAD_REQUEST),
				$(b5, HttpStatus.NOT_FOUND), $(b6, HttpStatus.NOT_FOUND),
				$(b7, HttpStatus.BAD_REQUEST), $(b71, HttpStatus.BAD_REQUEST),
				$(b8, HttpStatus.BAD_REQUEST), $(b9, HttpStatus.BAD_REQUEST));
	}

	@Parameters(method = "getBacklogForUpdateInvalid")
	@Test
	public void testUpdateBacklogInvalid(Backlog patch, HttpStatus status) {
		String url = URL_BASE + "1";
		Backlog beforeUpdate = rest.getForObject(url, Backlog.class);

		RestTestUtils.assertRestError(rest, HttpMethod.PUT, url, patch, status);

		// Make sure invalid update is rolled back
		Backlog b = rest.getForObject(url, Backlog.class);
		IObjectPatcher patcher = new ObjectPatcher();
		Assert.assertTrue(patcher.patchObject(b, beforeUpdate).isEmpty());
	}

}
