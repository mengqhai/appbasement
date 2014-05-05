package com.angularjsplay.e2e.rest;

import java.sql.Timestamp;

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

import static junitparams.JUnitParamsRunner.$;

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

	final static String URL_BASE = "http://localhost:8081/angularJsPlay/backlogs/";

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
		Backlog created = rest.postForObject(URL_BASE, toCreate, Backlog.class);
		Assert.assertNotNull(created.getId());
		toCreate.setId(created.getId());
		toCreate.setCreatedAt(created.getCreatedAt());
		RestTestUtils.assertBacklogsEqual(toCreate, created);
	}

	public Object[] getBacklogInvalid() {
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

		return $($(b1), $(b2), $(b3), $(b4));
	}

	@Parameters(method = "getBacklogInvalid")
	@Test
	public void testCreateBaclogInvalid(Backlog toCreate) {
		RestTestUtils.assertRestError(rest, HttpMethod.POST, URL_BASE,
				toCreate, HttpStatus.BAD_REQUEST);
	}

	@Test
	public void testDeleteBacklog() {
		String url = URL_BASE + "1";
		rest.delete(url);
		RestTestUtils.assertRestError(rest, HttpMethod.GET, url, null,
				HttpStatus.NOT_FOUND);
	}

	public Object[] getBacklogForUpdate() {
		Backlog b1 = new Backlog();
		b1.setName("Name changed");

		return $($(b1));
	}

	@Parameters(method = "getBacklogForUpdate")
	@Test
	public void testUpdateBacklog(Backlog patch) {
		String url = URL_BASE + "1";
		rest.put(url, patch);
		Backlog updated = rest.getForObject(url, Backlog.class);
		IObjectPatcher patcher = new ObjectPatcher();
		Assert.assertTrue(patcher.patchObject(updated, patch).isEmpty());
	}

}
