package com.appbasement.component;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import javax.persistence.Id;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.appbasement.model.Group;
import com.appbasement.model.User;

public class ObjectPatcherTest {

	public static class PrimitiveModel {
		int intField;
		boolean isGood;

		@Id
		Object id;

		public int getIntField() {
			return intField;
		}

		public void setIntField(int intField) {
			this.intField = intField;
		}

		public boolean isGood() {
			return isGood;
		}

		public void setGood(boolean isGood) {
			this.isGood = isGood;
		}

		public Object getId() {
			return id;
		}

		public void setId(Object id) {
			this.id = id;
		}

	}

	IObjectPatcher patcher;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		//patcher = new ObjectPatcher();
		patcher = new StrategyEnabledObjectPatcher();
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
		patch.setId(8l); // try to modify the Id
		patch.setEmail("mengqhai@gmail.com");
		patch.setUsername("user1"); // no change to the username
		Assert.assertNull(patch.getCreatedAt());
		Map<Field, PatchedValue> patchedInfo = patcher.patchObject(user, patch);

		Assert.assertEquals(1, patchedInfo.size());

		Assert.assertEquals(Long.valueOf(3l), user.getId()); // should not patch
																// @Id

		Assert.assertEquals("mengqhai@gmail.com", user.getEmail());
		Assert.assertEquals(createdAt, user.getCreatedAt());
		Assert.assertEquals("user1", user.getUsername());
		Assert.assertEquals("password", user.getPassword());
		Assert.assertEquals(Long.valueOf(3l), user.getId());

		Assert.assertEquals(1, user.getGroups().size());

		for (Group group : user.getGroups()) {
			Assert.assertEquals(group1, group);
		}
	}

	@Test
	public void testPatchObjectShouldNotSomeProperties() {
		PrimitiveModel target = new PrimitiveModel();
		target.setGood(false);
		target.setIntField(85);

		PrimitiveModel patch = new PrimitiveModel();
		patch.setGood(false);
		patch.setIntField(25);
		patch.setId("Hello");

		Map<Field, PatchedValue> patchedInfo = patcher.patchObject(target,
				patch);
		Assert.assertEquals(0, patchedInfo.size());
		Assert.assertEquals(85, target.getIntField());
		Assert.assertFalse(patch.isGood());
		Assert.assertNull(target.getId());
	}

}
