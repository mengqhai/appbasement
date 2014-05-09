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
import com.angularjsplay.model.Project;
import com.angularjsplay.model.Sprint;
import com.angularjsplay.persistence.util.ScrumTestConstants;
import com.appbasement.component.IObjectPatcher;
import com.appbasement.component.ObjectPatcher;
import com.appbasement.persistence.util.DBUnitHelper;
import com.appbasement.persistence.util.EmfHelper;

import static com.angularjsplay.persistence.util.ScrumTestConstants.*;

@RunWith(JUnitParamsRunner.class)
public class ProjectRestTest {

	RestTemplate rest;

	final static String URL_BASE = URL_BASE_COMMON + "/projects/";

	public ProjectRestTest() {
	}

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
	public void testGetProjects() {
		Project[] projects = rest.getForObject(URL_BASE, Project[].class);
		Assert.assertEquals(2, projects.length);
		for (Project project : projects) {
			Assert.assertNotNull(project.getName());
			Assert.assertNotNull(project.getId());
			Assert.assertNotNull(project.getCreatedAt());
		}
	}

	public Object[] getProjectParams() {
		Project p1 = new Project();
		p1.setId(1l);
		p1.setName("semper egestas, urna justo faucibus lectus, a sollicitudin orci sem");
		p1.setDesc("egestas. Aliquam nec enim. Nunc ut erat. Sed nunc est, mollis non, cursus non, egestas a, dui. Cras pellentesque.");
		p1.setCreatedAt(Timestamp.valueOf("2012-01-11 19:14:28"));

		Project p2 = new Project();
		p2.setId(2l);
		p2.setName("auctor odio a purus. Duis elementum, dui quis accumsan convallis,");
		p2.setDesc("tellus id nunc interdum feugiat. Sed nec metus facilisis lorem tristique aliquet. Phasellus fermentum convallis ligula."
				+ " Donec luctus aliquet odio. Etiam ligula tortor, dictum eu, placerat eget, venenatis a, magna. Lorem ipsum dolor sit amet"
				+ ", consectetuer adipiscing elit. Etiam laoreet, libero et tristique");
		p2.setCreatedAt(Timestamp.valueOf("2011-07-20 10:15:12"));
		return $($(p1), $(p2));
	}

	@Parameters(method = "getProjectParams")
	@Test
	public void testGetProject(Project expected) {
		Project project = rest.getForObject(URL_BASE + expected.getId(),
				Project.class);
		Assert.assertEquals(expected, project);
		Assert.assertEquals(expected.getId(), project.getId());
		Assert.assertEquals(expected.getName(), project.getName());
		Assert.assertEquals(expected.getCreatedAt().getTime(), project
				.getCreatedAt().getTime());
	}

	public Object[] getProjectToCreate() {
		Project p1 = new Project();
		p1.setName("A test project.");
		p1.setDesc("I should be created successfully!");
		return $($(p1));
	}

	@Parameters(method = "getProjectToCreate")
	@Test
	public void testCreateProject(Project toCreate) {
		Date now = new Date();
		Project created = rest.postForObject(URL_BASE, toCreate, Project.class);
		Assert.assertNotNull(created.getId());
		// should be created within 6 seconds
		Assert.assertTrue(Math.abs(now.getTime()
				- created.getCreatedAt().getTime()) < 6000l);
		toCreate.setCreatedAt(created.getCreatedAt());
		Assert.assertEquals(toCreate, created);
		Assert.assertEquals(toCreate.getName(), created.getName());
		Assert.assertEquals(toCreate.getDesc(), created.getDesc());
	}

	public Object[] getProjectInvalid() {
		Project p1 = new Project();

		Project p2 = new Project();
		StringBuilder bigName = new StringBuilder();
		for (int i = 0; i < 580; i++) {
			bigName.append("O");
		}
		p2.setName(bigName.toString());

		Project p3 = new Project();
		p3.setName("");

		return $($(p1, HttpStatus.BAD_REQUEST), $(p2, HttpStatus.BAD_REQUEST),
				$(p3, HttpStatus.BAD_REQUEST));
	}

	@Parameters(method = "getProjectInvalid")
	@Test
	public void testCreateProjectInvalid(Project toCreate, HttpStatus status) {
		RestTestUtils.assertRestError(rest, HttpMethod.POST, URL_BASE,
				toCreate, status);
	}

	@Test
	public void testDeleteProject() {
		String url = URL_BASE + "1";
		rest.delete(url);
		Project[] projects = rest.getForObject(URL_BASE, Project[].class);
		Assert.assertEquals(1, projects.length);
		for (Project project : projects) {
			Assert.assertNotEquals(Long.valueOf(1l), project.getId());
		}

		RestTestUtils.assertRestError(rest, HttpMethod.GET, url, null,
				HttpStatus.NOT_FOUND);
		// backlog & sprint also deleted
		Backlog[] backlogs = rest.getForObject(url + "/backlogs",
				Backlog[].class);
		Sprint[] sprints = rest.getForObject(url + "/sprints", Sprint[].class);
		Assert.assertEquals(0, backlogs.length);
		Assert.assertEquals(0, sprints.length);
	}

	@Test
	public void testDeleteProjectInvalid() {
		String url = URL_BASE + "1111";
		RestTestUtils.assertRestError(rest, HttpMethod.DELETE, url, null,
				HttpStatus.NOT_FOUND);
	}

	public Object[] getProjectForUpdate() {
		Project p1 = new Project();
		p1.setName("New name");

		Project p2 = new Project();
		p2.setDesc("Hello!");
		return $($(p1), $(p2));
	}

	@Parameters(method = "getProjectForUpdate")
	@Test
	public void testUpdateProject(Project patch) {
		rest.put(URL_BASE + "1", patch);
		Project updated = rest.getForObject(URL_BASE + "1", Project.class);
		IObjectPatcher patcher = new ObjectPatcher();
		Assert.assertTrue(patcher.patchObject(updated, patch).isEmpty());
	}

	public Object[] getProjectForUpdateInvalid() {
		Project p1 = new Project();
		p1.setName("");

		Project p2 = new Project();
		p2.setName("Invalid Url");

		return $($(URL_BASE + 1, p1, HttpStatus.BAD_REQUEST),
				$(URL_BASE + 999, p2, HttpStatus.NOT_FOUND));
	}

	@Parameters(method = "getProjectForUpdateInvalid")
	@Test
	public void testUpdateProjectInvalid(String url, Project patch,
			HttpStatus status) {
		RestTestUtils.assertRestError(rest, HttpMethod.PUT, url, patch, status);
	}

	@Test
	public void testUpdateProject() {
		Project patch = new Project();
		// createdAt should be ignored for update
		patch.setCreatedAt(new Date());
		rest.put(URL_BASE + "1", patch);

		Project updated = rest.getForObject(URL_BASE + "1", Project.class);
		Assert.assertNotEquals(patch.getCreatedAt().getTime(), updated
				.getCreatedAt().getTime());
	}

	public Object[] getBacklogsForProjectParams() {
		return $($(Long.valueOf(1), Long.valueOf(10l), Long.valueOf(10l)),
				$(Long.valueOf(2), Long.valueOf(10l), Long.valueOf(20l)));
	}

	@Parameters(method = "getBacklogsForProjectParams")
	@Test
	public void testGetBacklogsForProject(Long projectId, Long count,
			Long idOfFirst) {
		String commonUrl = URL_BASE + projectId + "/backlogs";
		RestTestUtils.assertPagibleChildren(rest, commonUrl, Backlog.class,
				count, idOfFirst);
	}

	public Object[] getSprintsForProjectParams() {
		return $($(Long.valueOf(1), Long.valueOf(5), Long.valueOf(8)),
				$(Long.valueOf(2), Long.valueOf(5), Long.valueOf(10)));
	}

	@Parameters(method = "getSprintsForProjectParams")
	@Test
	public void testGetSprintsForProject(Long projectId, Long count, Long idOfFirst) {
		String commonUrl = URL_BASE + projectId + "/sprints";
		RestTestUtils.assertPagibleChildren(rest, commonUrl, Sprint.class,
				count, idOfFirst);
	}

}
