package com.appbasement.persistence;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
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
import com.appbasement.persistence.util.TemplateWorker;
import com.appbasement.persistence.util.TestConstants;
import com.appbasement.persistence.util.UtUtil;

@RunWith(JUnitParamsRunner.class)
public class UserJpaDAOTest extends GenericJpaDAOTest<User, Long> {

	@Override
	public GenericJpaDAO<User, Long> createDao() {
		return new UserJpaDAO();
	}

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
	protected Object[] getIdsForRemove() {
		return $($(1l));
	}

	@Override
	protected Object[] getInvalidIdsForRemove() {
		return $($(0l, 1000l, -1000l));
	}

	@Override
	protected Object[] getParamEntitiesFound() {
		try {
			Group group1 = new Group("Porttitor Vulputate Posuere Limited")
					.setCreatedAt(Timestamp.valueOf("2013-03-01 01:30:27"));
			Group group2 = new Group("Admin").setCreatedAt(UtUtil
					.parseTimestamp("2011-11-27 06:40:37"));

			User user1 = new User("Carson", "XAE19DBV6VV").setEmail(
					"Vivamus.nibh.dolor@arcuiaculisenim.edu").setCreatedAt(
					Timestamp.valueOf("2013-03-01 22:26:26"));
			user1.addToGroup(group1);

			User user3 = new User("Walker", "LGH10KEX4GX").setEmail(
					"ac.mattis@ut.com").setCreatedAt(
					UtUtil.parseTimestamp("2013-03-01 18:17:46"));
			user3.addToGroup(group1);
			user3.addToGroup(group2);

			User user5 = new User("Risa", "CCN49GWW0UK").setEmail(
					"ultrices.sit@et.co.uk").setCreatedAt(
					Timestamp.valueOf("2014-07-26 13:22:13"));

			return $($(1l, user1), $(3l, user3), $(5l, user5));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Object[] getParamEntitiesNotFound() {
		return $($(-10000l, null), $(0l, null));
	}

	@Override
	public User testFindByIdID(final Long id, final User expected) {
		User actual = super.testFindByIdID(id, expected);
		// test groups association
		if (expected != null)
			assertEquals(expected.getGroups(), actual.getGroups());
		return actual;
	}

	@Override
	protected Object[] findAllAssertion() {
		int resultListSize = 5;
		Collection<User> mustContain = new ArrayList<User>();
		Object[] objArr = getParamEntitiesFound();
		for (Object obj : objArr) {
			Object[] entry = (Object[]) obj;
			mustContain.add((User) entry[1]);
		}
		return $($(resultListSize, mustContain));
	}

	@Override
	protected Object[] getPersistEntities() {
		User user = new User();
		user.setUsername("testuser");
		user.setPassword("guesswhat345");
		user.setEmail("testuser@dummy.com");
		user.setCreatedAt(new Date());
		DBUnitAssertionWork aWork = new DBUnitAssertionWork(this.getClass(),
				"testPersist", TestConstants.TABLE_USER);
		aWork.replaceCreatedAt(user.getCreatedAt());
		return $($(user, aWork));
	}

	@Override
	protected Object[] getPersistEntitiesInvalid() {
		// Ignore the testPersistInvalidEntity case in super class
		return $();
	}

	protected Object[] getPersistEntitiesValidationFail() {
		return $($(new User(null, "pass").setEmail("someadd@some.com")
				.setCreatedAt(new Date()),
				new User("user1", null).setEmail("someadd@some.com")
						.setCreatedAt(new Date()),
				new User().setCreatedAt(new Date()),
				new User("user", "pass").setCreatedAt(new Date())));
	}

	@Override
	@Test(expected = ConstraintViolationException.class)
	@Parameters(method = "getPersistEntitiesValidationFail")
	public void testPersistInvalidEntity(User invalidEntity) {
		super.testPersistInvalidEntity(invalidEntity);
	}

	@Override
	protected Object[] getMergeEntities() {
		final DBUnitAssertionWork aWork = new DBUnitAssertionWork(
				this.getClass(), "testMerge", TestConstants.TABLE_USER,
				"id in (1)");
		return $($(1l, aWork));
	}

	@Override
	protected void mergeUpdateInDetached(User entity,
			Map<String, Object> modifiedAtts) {
		switch (entity.getId().toString()) {
		case "1":
			String password = "modified";
			String email = "modified@abc.com";
			entity.setPassword(password);
			entity.setEmail(email);

			modifiedAtts.put("password", password);
			modifiedAtts.put("email", email);

			// Remove one group
			int groupCount = entity.getGroups().size();
			Iterator<Group> gIter = entity.getGroups().iterator();
			Group group = null;
			while (gIter.hasNext()) {
				group = gIter.next();
				break;
			}
			entity.removeFromGroup(group);
			assertEquals(groupCount - 1, entity.getGroups().size());

			modifiedAtts.put("groups", entity.getGroups());
		}
	}

	// For mergeImmutable test

	@Override
	protected Object[] getMergeImmutableEntities() {
		final DBUnitAssertionWork aWork = new DBUnitAssertionWork(
				this.getClass(), "testMergeImmutable",
				TestConstants.TABLE_USER, "id in (1)");
		return $($(1l, aWork));
	}

	@Override
	protected void mergeUpdateImmutableInDetached(User entity) {
		entity.setCreatedAt(new Date());
		entity.setUsername("somenewusername");
	}

	protected Object[] getUsersFindByUsername() {
		List<Object[]> params = new ArrayList<Object[]>();

		Object[] objArr = getParamEntitiesFound();
		for (Object obj : objArr) {
			Object[] entry = (Object[]) obj;
			User user = (User) entry[1];
			params.add($(user.getUsername(), user));
		}

		return params.toArray();
	}

	@Test
	@Parameters(method = "getUsersFindByUsername")
	public void testFindByUsername(final String username, User expected) {
		final IUserDAO userDao = (IUserDAO) dao;
		User user = new TemplateWorker<User>(dao.getEm()) {
			@Override
			protected void doIt() {
				setResult(userDao.findByUsername(username));
			}

		}.getResult();

		assertEquals(expected, user);
		assertEquals(expected.getGroups(), user.getGroups());
		assertEquals(expected.getPassword(), user.getPassword());
	}

	protected Object[] getUsersFindByUsernameNotFound() {
		return $($("NoSuchUserName"));
	}

	@Test
	@Parameters(method = "getUsersFindByUsernameNotFound")
	public void testFindByUsernameNotFound(final String username) {
		final IUserDAO userDao = (IUserDAO) dao;
		User user = new TemplateWorker<User>(dao.getEm()) {
			@Override
			protected void doIt() {
				setResult(userDao.findByUsername(username));
			}

		}.getResult();
		assertNull(user);
	}
}
