package com.angularjsplay.e2e.rest;

import static junitparams.JUnitParamsRunner.$;

import java.io.IOException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.angularjsplay.model.Project;
import com.angularjsplay.mvc.rest.error.RestError;
import com.angularjsplay.persistence.util.ScrumTestConstants;
import com.appbasement.component.IObjectPatcher;
import com.appbasement.component.ObjectPatcher;
import com.appbasement.persistence.util.DBUnitHelper;
import com.appbasement.persistence.util.EmfHelper;

@RunWith(JUnitParamsRunner.class)
public class ProjectRestTest {

	RestTemplate rest;

	final static String URL_BASE = "http://localhost:8081/angularJsPlay/projects/";

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
		// should be created within 3 seconds
		Assert.assertTrue(Math.abs(now.getTime()
				- created.getCreatedAt().getTime()) < 3000l);
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

		return $($(p1), $(p2), $(p3));
	}

	@Parameters(method = "getProjectInvalid")
	@Test
	public void testCreateProjectFail(Project toCreate) {
		rest.setErrorHandler(new DefaultResponseErrorHandler() {
			@Override
			public void handleError(ClientHttpResponse response)
					throws IOException {
				// do nothing
			}
		});

		ResponseEntity<RestError> response = rest.postForEntity(URL_BASE,
				toCreate, RestError.class);
		Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		RestError error = response.getBody();
		Assert.assertEquals(response.getStatusCode().value(), error.getCode());
		Assert.assertNotNull(error.getMessage());
	}

	@Test
	public void testDeleteProject() {
		rest.delete(URL_BASE + "1");
		Project[] projects = rest.getForObject(URL_BASE, Project[].class);
		Assert.assertEquals(1, projects.length);
		for (Project project : projects) {
			Assert.assertNotEquals(Long.valueOf(1l), project.getId());
		}
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

}
