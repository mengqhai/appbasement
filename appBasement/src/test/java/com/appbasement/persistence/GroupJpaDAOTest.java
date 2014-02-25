package com.appbasement.persistence;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.validation.ConstraintViolationException;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.appbasement.model.Group;
import com.appbasement.model.User;
import com.appbasement.persistence.util.DBUnitAssertionWork;
import com.appbasement.persistence.util.EmfHelper;
import com.appbasement.persistence.util.TestConstants;
import com.appbasement.persistence.util.UtUtil;

@RunWith(JUnitParamsRunner.class)
public class GroupJpaDAOTest extends GenericJpaDAOTest<Group, Long> {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		EmfHelper.initEmf();
		assertNotNull(EmfHelper.getEmf());
		assertTrue(EmfHelper.getEmf().isOpen());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		EmfHelper.closeEmf();
	}

	@Override
	public GenericJpaDAO<Group, Long> createDao() {
		return new GroupJpaDAO();
	}

	@Override
	protected Object[] getIdsForRemove() {
		return $($(1l));
	}

	@Override
	protected Object[] getInvalidIdsForRemove() {
		return $($(0l, 1000l, -1000l));
	}

	@Override
	protected Object[] getParamEntitiesNotFound() {
		return $($(888l, null), $(0l, null), $(-10l, null));
	}

	@Override
	protected Object[] getParamEntitiesFound() {
		Group group1 = new Group("Porttitor Vulputate Posuere Limited")
				.setCreatedAt(Timestamp.valueOf("2013-03-01 01:30:27"));
		Group group11 = new Group("Admin").setCreatedAt(UtUtil
				.parseTimestamp("2011-11-27 06:40:37"));

		User user1 = new User("Carson", "XAE19DBV6VV").setEmail(
				"Vivamus.nibh.dolor@arcuiaculisenim.edu").setCreatedAt(
				Timestamp.valueOf("2013-03-01 22:26:26"));
		user1.addToGroup(group1);

		User user2 = new User("Sylvester", "IVQ76QMJ1VK").setEmail(
				"ipsum@sollicitudin.org").setCreatedAt(
				Timestamp.valueOf("2014-07-23 15:28:24"));
		user2.addToGroup(group11);

		User user3 = new User("Walker", "LGH10KEX4GX").setEmail(
				"ac.mattis@ut.com").setCreatedAt(
				UtUtil.parseTimestamp("2013-03-01 18:17:46"));
		user3.addToGroup(group1);
		user3.addToGroup(group11);
		return $($(1l, group1), $(11l, group11));
	}

	@Override
	public Group testFindByIdID(Long id, Group expected) {
		Group group = super.testFindByIdID(id, expected);

		// test users association
		if (expected != null) {
			assertEquals(expected.getUsers(), group.getUsers());
		}
		return group;
	}

	@Override
	protected Object[] findAllAssertion() {
		int resultListSize = 2;
		Collection<Group> mustContain = new ArrayList<Group>();
		Object[] objArr = getParamEntitiesFound();
		for (Object obj : objArr) {
			Object[] entry = (Object[]) obj;
			mustContain.add((Group) entry[1]);
		}
		return $($(resultListSize, mustContain));
	}

	@Override
	protected Object[] getPersistEntities() {
		Group group = new Group();
		group.setName("testgroup");
		DBUnitAssertionWork aWork = new DBUnitAssertionWork(
				GroupJpaDAOTest.class, "testPersist", TestConstants.TABLE_GROUP);
		aWork.replaceCreatedAt(group.getCreatedAt());
		return $($(group, aWork));
	}

	@Override
	protected Object[] getPersistEntitiesInvalid() {
		return $();
	}

	protected Object[] getPersistEntitiesValidationFail() {
		return $($(new Group()));
	}

	@Override
	@Test(expected = ConstraintViolationException.class)
	@Parameters(method = "getPersistEntitiesValidationFail")
	public void testPersistInvalidEntity(Group invalidEntity) {
		super.testPersistInvalidEntity(invalidEntity);
	}

	@Override
	protected Object[] getMergeEntities() {
		final DBUnitAssertionWork aWork = new DBUnitAssertionWork(
				GroupJpaDAOTest.class, "testMerge", TestConstants.TABLE_GROUP,
				"id in (1)");
		return $($(1l, aWork));
	}

	@Override
	protected void mergeUpdateInDetached(Group entity,
			Map<String, Object> modifiedAtts) {
		String name = "modified";
		entity.setName(name);

		int userCount = entity.getUsers().size();
		Iterator<User> uIter = entity.getUsers().iterator();
		User userToRemove = null;
		while (uIter.hasNext()) {
			userToRemove = uIter.next();
			break;
		}
		userToRemove.removeFromGroup(entity);

		assertEquals(userCount - 1, entity.getUsers().size());
		modifiedAtts.put("name", name);
		modifiedAtts.put("users", entity.getUsers());
	}

	@Override
	protected Object[] getMergeImmutableEntities() {
		final DBUnitAssertionWork aWork = new DBUnitAssertionWork(
				this.getClass(), "testMergeImmutable",
				TestConstants.TABLE_GROUP, "id in (1)");
		return $(1l, aWork);
	}

	@Override
	protected void mergeUpdateImmutableInDetached(Group entity) {
		entity.setCreatedAt(new Date());
	}

}
