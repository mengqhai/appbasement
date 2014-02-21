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

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class TemplateTest {

	protected Object[] getEqualsEntity() {
		Date date = new Date();
		return $(
				$(new Template(), new Template()),
				$(new Template().setName("Hello"),
						new Template().setName("Hello")),
				$(new Template().setLastUpdate(date),
						new Template().setLastUpdate(date)),
				$(new Template().setLastUpdate(date).setName("Hello"),
						new Template().setLastUpdate(date).setName("Hello")));
	}

	@Test
	@Parameters(method = "getEqualsEntity")
	public void testEqualsHashCode(String template1, String template2) {
		assertEquals(template1, template2);
		assertEquals(template1.hashCode(), template2.hashCode());
	}

	protected Object[] getNotEqualsEntity() {
		return $(
				$(new Template(), new Template().setName("Hello")),
				$(new Template(), new Template().setLastUpdate(new Date())),
				$(new Template().setName("Hello"),
						new Template().setLastUpdate(new Date())));
	}

	@Test
	@Parameters(method = "getNotEqualsEntity")
	public void testNotEquals(Template t1, Template t2) {
		assertNotEquals(t1, t2);
	}

	@Test
	public void testHashSetAccess() {
		Set<Template> set = new HashSet<Template>();
		Template t1 = new Template().setName("T1").setLastUpdate(new Date());
		Template t2 = new Template().setName("T2").setLastUpdate(new Date());

		set.add(t1);
		set.add(t2);

		t1.setId(1l);
		t2.setId(2l);

		// set Id doesn't impact the set
		assertTrue(set.contains(t1));
		assertTrue(set.contains(t2));
		assertEquals(2, set.size());

		Template t3 = new Template().setName("T1").setLastUpdate(
				t1.getLastUpdate());
		assertTrue(set.contains(t3));

		t1.setName("Changed");
		assertFalse(set.contains(t1));
		set.add(t1);
		assertEquals(3, set.size());

		t2.setLastUpdate(new Date(t2.getLastUpdate().getTime() + 100));
		assertFalse(set.contains(t2));
		set.add(t2);
		assertEquals(4, set.size());
	}

}
