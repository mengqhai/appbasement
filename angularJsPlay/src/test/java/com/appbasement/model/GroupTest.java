package com.appbasement.model;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertEquals;
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
public class GroupTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testHashSetAccess() {
		Set<Group> groupSet = new HashSet<Group>();
		Group group1 = new Group("group1");
		Group group2 = new Group("group2");
		groupSet.add(group1);
		groupSet.add(group2);
		assertEquals(2, groupSet.size());
		assertTrue(groupSet.contains(group1));
		assertTrue(groupSet.contains(group2));

		// hash code changed, still accessible
		group1.setName("group111");
		group2.setName("group222");
		assertEquals(2, groupSet.size());
		assertTrue(groupSet.contains(group1));
		assertTrue(groupSet.contains(group2));
	}

	private void forceSameCreatedAt(Group group1, Group group2) {
		group2.setCreatedAt(new Date(group1.getCreatedAt().getTime()));
	}

	protected Object[] getNotEqualsGroups() {
		Group group1 = new Group();
		Group group2 = new Group("group2");
		forceSameCreatedAt(group1, group2);

		// different group name
		Group group11 = new Group("group1");
		Group group22 = new Group("group2");
		forceSameCreatedAt(group11, group22);

		return $($(new Group(), null), $(group1, group2), $(group11, group22));
	}

	@Test
	@Parameters(method = "getNotEqualsGroups")
	public void testNotEquals(Group group1, Group group2) {
		assertNotEquals(group1, group2);
	}

	protected Object[] getEqualsGroups() {
		// empty instance
		Group group1 = new Group();
		Group group2 = new Group();
		forceSameCreatedAt(group1, group2);

		// same group name
		Group group11 = new Group("group");
		Group group22 = new Group("group");
		forceSameCreatedAt(group11, group22);

		// sub class should pass equals (book JPWH CH9.2.3.1)
		Group group111 = new Group("group");
		Group group222 = new Group() {
			private static final long serialVersionUID = 1L;
		};
		group222.setName("group");
		forceSameCreatedAt(group111, group222);

		return $($(group1, group2), $(group11, group22), $(group111, group222));
	}

	@Test
	@Parameters(method = "getEqualsGroups")
	public void testEqualsHashCode(Group group1, Group group2) {
		assertEquals(group1, group2);
		// equal instance must has the same hash code
		assertEquals(group1.hashCode(), group2.hashCode());
	}

}
