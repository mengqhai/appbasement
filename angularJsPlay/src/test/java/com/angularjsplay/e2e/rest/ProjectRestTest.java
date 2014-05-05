package com.angularjsplay.e2e.rest;

import static junitparams.JUnitParamsRunner.$;

import java.sql.Timestamp;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.web.client.RestTemplate;

import com.angularjsplay.model.Project;
import com.angularjsplay.persistence.util.ScrumTestConstants;
import com.appbasement.persistence.util.DBUnitHelper;
import com.appbasement.persistence.util.EmfHelper;

@RunWith(JUnitParamsRunner.class)
public class ProjectRestTest {

	RestTemplate rest;

	String urlBase = "http://localhost:8081/angularJsPlay/";

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
		Project[] projects = rest.getForObject(urlBase + "projects",
				Project[].class);
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
	public void testGetProject1(Project expected) {
		Project project = rest.getForObject(
				urlBase + "projects/" + expected.getId(), Project.class);
		Assert.assertEquals(expected, project);
		Assert.assertEquals(expected.getId(), project.getId());
		Assert.assertEquals(expected.getName(), project.getName());
		Assert.assertEquals(expected.getCreatedAt().getTime(), project
				.getCreatedAt().getTime());
	}

}
