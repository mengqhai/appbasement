package com.appbasement.model;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class UserTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddRemoveGroupException() {

		User user1 = new User();
		// null test
		user1.addToGroup(null);
	}

	@Test
	public void testAddRemoveGroup() {
		User user1 = new User();
		Group group1 = new Group();
		user1.addToGroup(group1);

		assertTrue(group1.getUsers().contains(user1));
		assertEquals(1, group1.getUsers().size());

		assertTrue(user1.getGroups().contains(group1));
		assertTrue(user1.getGroups().contains(group1));

		user1.removeFromGroup(group1);

		assertTrue(user1.getGroups().isEmpty());
		assertTrue(group1.getUsers().isEmpty());
	}

	@Test
	public void testHashSetAccess() {
		Set<User> userSet = new HashSet<User>();
		User user1 = new User("user1");
		User user2 = new User("user2");
		userSet.add(user1);
		userSet.add(user2);
		
		user1.setId(1l);
		user2.setId(2l);
		
		assertEquals(2, userSet.size());
		assertTrue(userSet.contains(user1));
		assertTrue(userSet.contains(user2));

		// setting password won't change hash code
		user1.setPassword("password1");
		user2.setPassword("password2");
		assertTrue(userSet.contains(user1));
		assertTrue(userSet.contains(user2));

		// same user add twice
		User user1_2 = new User(user1.getUsername());
		user1_2.setPassword(user1.getPassword());
		user1_2.setEmail(user1.getEmail());
		forceSameCreatedAt(user1, user1_2);
		userSet.add(user1_2);
		assertEquals(2, userSet.size());
		assertTrue(userSet.contains(user1_2));

		// hash code changed, no longer accessible
		user1.setEmail("email1");
		user2.setEmail("email2");
		assertEquals(2, userSet.size());
		assertFalse(userSet.contains(user1));
		assertFalse(userSet.contains(user2));
	}

	private void forceSameCreatedAt(User user1, User user2) {
		user2.setCreatedAt(new Date(user1.getCreatedAt().getTime()));
	}

	protected Object[] getEqualsUsers() {
		// empty instance test
		User user1 = new User();
		User user2 = new User();
		forceSameCreatedAt(user1, user2);

		// same username, same password
		User user11 = new User("user1", "password");
		User user22 = new User("user1", "password");
		forceSameCreatedAt(user11, user22);

		// same username, same email
		User user111 = new User("user1");
		user111.setEmail("user1@gmail.com");
		User user222 = new User("user1");
		user222.setEmail("user1@gmail.com");
		forceSameCreatedAt(user111, user222);

		// sub class should pass equals (book JPWH CH9.2.3.1)
		User user1111 = new User("user1");
		user1111.setEmail("user1@gmail.com");
		@SuppressWarnings("serial")
		User user2222 = new User() {
		};
		user2222.setUsername("user1");
		user2222.setEmail("user1@gmail.com");
		forceSameCreatedAt(user1111, user2222);

		return $($(user1, user2), $(user11, user22), $(user111, user222),
				$(user1111, user2222));
	}

	@Test
	@Parameters(method = "getEqualsUsers")
	public void testEqualsHashCode(User expected, User actual) {
		assertEquals(expected, actual);
		// equal instance must has the same hash code
		assertEquals(expected.hashCode(), actual.hashCode());
	}

	protected Object[] getNotEqualsUsers() {
		// same username, different password
		User user1 = new User("user1", "password1");
		User user2 = new User("user1", "password2");
		forceSameCreatedAt(user1, user2);

		// same username, same email, different creation time
		User user11 = new User("user1").setEmail("user1@gmail.com");
		User user22 = new User("user1").setEmail("user1@gmail.com");
		user22.setCreatedAt(new Date(user11.getCreatedAt().getTime() + 1000));

		// same username, one null email
		User user111 = new User("user1").setEmail("user1@gmail.com");
		User user222 = new User("user1");
		forceSameCreatedAt(user111, user222);

		// different username, different email
		User user1111 = new User("user1").setEmail("user1@gmail.com");
		User user2222 = new User("user2").setEmail("user2@gmail.com");
		forceSameCreatedAt(user1111, user2222);

		return $($(new User("user1"), null), $(user11, user22),
				$(user111, user222), $(user1111, user2222));
	}

	@Test
	@Parameters(method = "getNotEqualsUsers")
	public void testNotEquals(User user1, User user2) {
		assertNotEquals(user1, user2);
	}

}
