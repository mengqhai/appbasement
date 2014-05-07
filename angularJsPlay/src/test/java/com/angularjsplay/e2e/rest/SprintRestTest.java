package com.angularjsplay.e2e.rest;

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
import com.angularjsplay.model.Backlog;
import com.angularjsplay.model.Sprint;
import com.angularjsplay.persistence.util.ScrumTestConstants;
import com.appbasement.persistence.util.DBUnitHelper;
import com.appbasement.persistence.util.EmfHelper;
import static com.angularjsplay.persistence.util.ScrumTestConstants.*;

@RunWith(JUnitParamsRunner.class)
public class SprintRestTest {

	RestTemplate rest;

	final static String URL_BASE = URL_BASE_COMMON + "/sprints/";

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

	@Test
	public void testGetSprints() {
		Sprint[] sprints = rest.getForObject(URL_BASE, Sprint[].class);
		Assert.assertEquals(10, sprints.length);
		for (Sprint s : sprints) {
			Assert.assertNotNull(s.getId());
			Assert.assertNotNull(s.getName());
			Assert.assertNotNull(s.getCreatedAt());
		}
	}

	public Object[] getSprintsParams() {
		Sprint s1 = new Sprint();
		s1.setId(1l);
		s1.setName("at sem molestie sodales. Mauris blandit enim consequat purus. Maecenas");
		s1.setDesc("convallis in, cursus et, eros. Proin ultrices. Duis volutpat nunc sit amet metus. "
				+ "Aliquam erat volutpat. Nulla facilisis. Suspendisse commodo tincidunt nibh. Phasellus "
				+ "nulla. Integer vulputate, risus a ultricies adipiscing, enim mi tempor lorem, eget moll"
				+ "is lectus pede et risus. Quisque libero lacus, varius et, euismod et, commodo at, libero"
				+ ". Morbi accumsan laoreet ipsum. Curabitur consequat, lectus sit amet luctus");
		s1.setCapacity((short) 30);
		s1.setStartAt(Timestamp.valueOf("2014-09-04 16:59:03"));
		s1.setEndAt(Timestamp.valueOf("2015-02-26 19:46:11"));
		s1.setCreatedAt(Timestamp.valueOf("2013-07-31 18:02:56"));
		s1.setProjectId(2l);

		Sprint s2 = new Sprint();
		s2.setId(3l);
		s2.setName("turpis egestas. Fusce aliquet magna a neque. Nullam ut nisi");
		s2.setDesc("nulla magna, malesuada vel, convallis in, cursus et, eros. Proin ultrices. Duis volutpat nunc sit amet metus. "
				+ "Aliquam erat volutpat. Nulla facilisis. Suspendisse commodo tincidunt nibh. Phasellus nulla. Integer vulputate, "
				+ "risus a ultricies adipiscing, enim mi tempor lorem, eget mollis lectus pede et risus. Quisque libero lacus, vari"
				+ "us et, euismod et, commodo at, libero. Morbi accumsan laoreet ipsum. Curabitur consequat, lectus sit amet luctus "
				+ "vulputate, nisi sem semper erat, in consectetuer ipsum");
		s2.setCapacity((short) 30);
		s2.setStartAt(Timestamp.valueOf("2014-05-14 02:35:08"));
		s2.setEndAt(Timestamp.valueOf("2015-03-25 15:52:44"));
		s2.setCreatedAt(Timestamp.valueOf("2013-11-07 17:05:03"));
		s2.setProjectId(1l);
		return $($(s1), $(s2));
	}

	@Parameters(method = "getSprintsParams")
	@Test
	public void testGetSprint(Sprint expected) {
		Sprint actual = rest.getForObject(URL_BASE + expected.getId(),
				Sprint.class);
		Assert.assertEquals(expected, actual);
		Assert.assertEquals(expected.getId(), actual.getId());
		Assert.assertEquals(expected.getName(), actual.getName());
		Assert.assertEquals(expected.getDesc(), actual.getDesc());
		Assert.assertEquals(expected.getCapacity(), expected.getCapacity());
		Assert.assertEquals(expected.getStartAt().getTime(), actual
				.getStartAt().getTime());
		Assert.assertEquals(expected.getEndAt().getTime(), actual.getEndAt()
				.getTime());
		Assert.assertEquals(expected.getCreatedAt().getTime(), actual
				.getCreatedAt().getTime());
		Assert.assertEquals(expected.getProjectId(), actual.getProjectId());
	}

	@Test
	public void testGetSprintInvalid() {
		RestTestUtils.assertRestError(rest, HttpMethod.GET, URL_BASE + "13255",
				null, HttpStatus.NOT_FOUND);
	}

	public Object[] getSprintToCreate() {
		Sprint s1 = new Sprint();
		s1.setName("Test sprint");
		s1.setDesc("description");
		s1.setStartAt(Timestamp.valueOf("2013-04-29 07:42:50"));
		s1.setEndAt(Timestamp.valueOf("2013-08-29 07:42:50"));
		s1.setProjectId(1l);
		s1.setCapacity((short) 15);

		Sprint s2 = new Sprint();
		s2.setName("Test sprint2");
		s2.setProjectId(2l);
		return $($(s1), $(s2));
	}

	@Parameters(method = "getSprintToCreate")
	@Test
	public void testCreateSprint(Sprint toCreate) {
		Date now = new Date();
		Sprint created = rest.postForObject(URL_BASE, toCreate, Sprint.class);
		Assert.assertNotNull(created.getId());
		Assert.assertNotNull(created.getCreatedAt());
		Assert.assertTrue(Math.abs(created.getCreatedAt().getTime()
				- now.getTime()) < 3000);

		toCreate.setCreatedAt(created.getCreatedAt());
		Assert.assertEquals(toCreate, created);
		Assert.assertEquals(toCreate.getName(), created.getName());
		Assert.assertEquals(toCreate.getProjectId(), created.getProjectId());

		if (toCreate.getDesc() != null)
			Assert.assertEquals(toCreate.getDesc(), created.getDesc());
		else
			Assert.assertNull(created.getDesc());

		if (toCreate.getCapacity() != null)
			Assert.assertEquals(toCreate.getCapacity(), created.getCapacity());
		else
			Assert.assertNull(created.getCapacity());

		if (toCreate.getStartAt() != null)
			Assert.assertEquals(toCreate.getStartAt().getTime(), created
					.getStartAt().getTime());
		else
			Assert.assertNull(created.getStartAt());

		if (toCreate.getEndAt() != null)
			Assert.assertEquals(toCreate.getEndAt().getTime(), created
					.getEndAt().getTime());
		else
			Assert.assertNull(created.getEndAt());

		if (toCreate.getCreatedAt() != null)
			Assert.assertEquals(toCreate.getCreatedAt().getTime(), created
					.getCreatedAt().getTime());
		else
			Assert.assertNull(toCreate.getCreatedAt());
	}

	public Object[] getSprintToCreateInvalid() {
		Sprint s1 = new Sprint();
		s1.setProjectId(1l);

		Sprint s2 = new Sprint();
		s2.setName("");
		s1.setProjectId(1l);

		Sprint s3 = new Sprint();
		StringBuilder bigName = new StringBuilder();
		for (int i = 0; i < 280; i++) {
			bigName.append("0");
		}
		s3.setName(bigName.toString());
		s3.setProjectId(1l);

		Sprint s4 = new Sprint();
		s4.setName("No project id");

		Sprint s5 = new Sprint();
		s5.setProjectId(10000l);
		s5.setName("No such project");

		Sprint s6 = new Sprint();
		s6.setProjectId(1l);
		s6.setName("Invalid capacity");
		s6.setCapacity((short) -5);

		Sprint s7 = new Sprint();
		s7.setName("End earilier than start");
		s7.setProjectId(1l);
		s7.setEndAt(Timestamp.valueOf("2013-04-29 07:42:50"));
		s7.setStartAt(Timestamp.valueOf("2014-04-29 07:42:50"));

		return $($(s1, HttpStatus.BAD_REQUEST), $(s2, HttpStatus.BAD_REQUEST),
				$(s3, HttpStatus.BAD_REQUEST), $(s4, HttpStatus.BAD_REQUEST),
				$(s5, HttpStatus.NOT_FOUND), $(s6, HttpStatus.BAD_REQUEST),
				$(s7, HttpStatus.BAD_REQUEST));
	}

	@Parameters(method = "getSprintToCreateInvalid")
	@Test
	public void testCreateSprintInvalid(Sprint toCreate, HttpStatus status) {
		RestTestUtils.assertRestError(rest, HttpMethod.POST, URL_BASE,
				toCreate, status);
	}

	@Test
	public void testDeleteSprint() {
		String url = URL_BASE + "1";
		rest.delete(url);
		RestTestUtils.assertRestError(rest, HttpMethod.GET, url, null,
				HttpStatus.NOT_FOUND);
		Backlog[] backlogs = rest.getForObject(url + "/backlogs",
				Backlog[].class);
		Assert.assertTrue(backlogs.length == 0);
		// backlog #1 should not be deleted
		Backlog b1 = rest.getForObject(BacklogRestTest.URL_BASE + "1",
				Backlog.class);
		Assert.assertNotNull(b1);
	}

	@Test
	public void testDeleteSprintInvalid() {
		String url = URL_BASE + "99999";
		RestTestUtils.assertRestError(rest, HttpMethod.DELETE, url, null,
				HttpStatus.NOT_FOUND);
	}

}