package com.appbasement.component;

import java.sql.Timestamp;
import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.appbasement.model.Group;
import com.appbasement.model.User;

public class ObjectPatcherTest {

	IObjectPatcher patcher;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		patcher = new ObjectPatcher();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPatchObject() {
		User user = new User("user1", "password");
		user.setCreatedAt(Timestamp.valueOf("2012-01-01 20:40:05"));
		Date createdAt = user.getCreatedAt();
		user.setEmail("mqhnow1@sina.com");
		user.setId(3l);

		Group group1 = new Group();
		user.addToGroup(group1);

		User patch = new User();
		patch.setEmail("mengqhai@gmail.com");
		Assert.assertNull(patch.getCreatedAt());
		patcher.patchObject(user, patch);
		Assert.assertEquals("mengqhai@gmail.com", user.getEmail());
		Assert.assertEquals(createdAt, user.getCreatedAt());
		Assert.assertEquals("user1", user.getUsername());
		Assert.assertEquals("password", user.getPassword());
		Assert.assertEquals(Long.valueOf(3l), user.getId());
	}

}
