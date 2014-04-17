package com.appbasement.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;

import junitparams.Parameters;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.appbasement.model.User;
import com.appbasement.persistence.util.DBUnitAssertionWork;
import com.appbasement.persistence.util.DBUnitHelper;
import com.appbasement.persistence.util.EmfHelper;
import com.appbasement.persistence.util.TemplateWorker;
import com.appbasement.persistence.util.UtUtil;

public abstract class GenericJpaDAOTest<T, ID extends Serializable> {

	protected GenericJpaDAO<T, ID> dao = null;

	public abstract GenericJpaDAO<T, ID> createDao();

	@Before
	public void setUp() throws Exception {
		DBUnitHelper.cleanAll(EmfHelper.getEmf());
		DBUnitHelper.importSmallDataSet(EmfHelper.getEmf());
		dao = createDao();
		dao.setEm(EmfHelper.getEmf().createEntityManager());
	}

	@After
	public void tearDown() throws Exception {
		dao.getEm().close();
	}

	/**
	 * used by testRemove(final ID id)
	 * 
	 * @return
	 */
	protected abstract Object[] getIdsForRemove();

	@Test
	@Parameters(method = "getIdsForRemove")
	public void testRemove(final ID id) {
		final T entity = dao.findById(id);
		assertNotNull(entity);
		new TemplateWorker<T>(dao.getEm()) {
			@Override
			protected void doIt() {
				dao.remove(entity);
			}
		};

		// the record is no longer there
		new TemplateWorker<T>(dao.getEm()) {
			@Override
			protected void doIt() {
				assertNull(dao.findById(id));
			}
		};
	}

	protected abstract Object[] getInvalidIdsForRemove();

	@Test(expected = EntityNotFoundException.class)
	@Parameters(method = "getInvalidIdsForRemove")
	public void testRemoveInvalid(final ID id) {
		final T entity = dao.getReference(id);
		new TemplateWorker<T>(dao.getEm()) {
			@Override
			protected void doIt() {
				dao.remove(entity);
			}
		};
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveNull() {
		new TemplateWorker<T>(dao.getEm()) {
			@Override
			protected void doIt() {
				dao.remove(null);
			}
		};
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindByIDNull() {
		new TemplateWorker<T>(dao.getEm()) {
			@Override
			protected void doIt() {
				dao.findById(null);
			}
		};
	}

	protected abstract Object[] getParamEntitiesNotFound();

	protected abstract Object[] getParamEntitiesFound();

	protected Object[] getParamEntities() {
		return UtUtil.mergeArray(getParamEntitiesFound(),
				getParamEntitiesNotFound());
	}

	@Test
	@Parameters(method = "getParamEntitiesFound")
	public T testFindByIdID(final ID id, final T expected) {
		TemplateWorker<T> worker = new TemplateWorker<T>(dao.getEm()) {
			@Override
			protected void doIt() {
				T actual = dao.findById(id);
				assertEquals(expected, actual);
				setResult(actual);
			}
		};
		return worker.getResult();
	}

	@Test
	@Parameters(method = "getParamEntitiesFound")
	public void testGetReference(final ID id, final T expected) {
		new TemplateWorker<T>(dao.getEm()) {
			@Override
			protected void doIt() {
				T actual = dao.getReference(id);
				assertEquals(expected, actual);
				setResult(actual);
			}
		};
	}

	@Test(expected = EntityNotFoundException.class)
	@Parameters(method = "getParamEntitiesNotFound")
	public void testGetReferenceNotFound(final ID id, final T expected) {
		new TemplateWorker<T>(dao.getEm()) {
			@Override
			protected void doIt() {
				T actual = dao.getReference(id);
				Hibernate.initialize(actual);
			}
		};
	}

	protected abstract Object[] findAllAssertion();

	@Test
	@Parameters(method = "findAllAssertion")
	public void testFindAll(int resultListSize, Collection<T> mustContain) {
		TemplateWorker<List<T>> worker = new TemplateWorker<List<T>>(
				dao.getEm()) {
			@Override
			protected void doIt() {
				setResult(dao.findAll());
			}
		};
		List<T> result = worker.getResult();
		assertEquals(resultListSize, result.size());
		assertTrue(result.containsAll(mustContain));
	}

	protected abstract Object[] getPersistEntities();

	@Test
	@Parameters(method = "getPersistEntities")
	public void testPersist(final T entity, final DBUnitAssertionWork aWork) {
		new TemplateWorker<Object>(dao.getEm()) {
			@Override
			protected void doIt() {
				dao.persist(entity);
			}
		};
		assertNotNull(UtUtil.getField(entity, "id"));

		long id = (Long) UtUtil.getField(entity, "id");
		assertTrue(id > 0);

		aWork.setWhereCondition("id=" + id);
		aWork.replaceId(id);

		final Session session = dao.getEm().unwrap(Session.class);
		new TemplateWorker<T>(dao.getEm()) {
			@Override
			protected void doIt() {
				session.doWork(aWork);
			}
		};
	}

	protected abstract Object[] getPersistEntitiesInvalid();

	@Test(expected = PersistenceException.class)
	@Parameters(method = "getPersistEntitiesInvalid")
	public void testPersistInvalidEntity(final T invalidEntity) {
		new TemplateWorker<T>(dao.getEm()) {
			@Override
			protected void doIt() {
				dao.persist(invalidEntity);
			}
		};
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPersistNullEntity() {
		new TemplateWorker<T>(dao.getEm()) {
			@Override
			protected void doIt() {
				dao.persist(null);
			}
		};
	}

	/**
	 * Associated with the <code>mergeUpdateInDetached()</code> method
	 * 
	 * @return
	 */
	protected abstract Object[] getMergeEntities();

	/**
	 * Update the entity got from <code>getMergeEntities()</code> in detached
	 * status.
	 * 
	 * 
	 * @param entity
	 * @param modifiedAtts
	 */
	protected abstract void mergeUpdateInDetached(T entity,
			Map<String, Object> modifiedAtts);

	/**
	 * Depends on method <code>getMergeEntities()</code> and
	 * <code>mergeUpdateInDetached()</code>
	 * 
	 * @param id
	 * @param aWork
	 */
	@Test
	@Parameters(method = "getMergeEntities")
	public void testMerge(final ID id, final DBUnitAssertionWork aWork) {
		final T entity = new TemplateWorker<T>(dao.getEm()) {
			@Override
			protected void doIt() {
				setResult(dao.findById(id));
			}
		}.getResult();

		Map<String, Object> modifiedAtts = new HashMap<String, Object>();
		mergeUpdateInDetached(entity, modifiedAtts);

		new TemplateWorker<T>(dao.getEm()) {
			@Override
			protected void doIt() {
				dao.merge(entity);
			}
		};

		dao.getEm().detach(entity);

		T entity2 = new TemplateWorker<T>(dao.getEm()) {
			@Override
			protected void doIt() {
				Object idObj = UtUtil.getField(entity, "id");
				@SuppressWarnings("unchecked")
				ID id = (ID) idObj;
				setResult(dao.findById(id));
				Hibernate.initialize(getResult());
			}
		}.getResult();
		for (Map.Entry<String, Object> entry : modifiedAtts.entrySet()) {
			Object actual = UtUtil.getField(entity2, entry.getKey());
			assertEquals(entry.getValue(), actual);
		}

		final Session session = dao.getEm().unwrap(Session.class);
		new TemplateWorker<User>(dao.getEm()) {
			@Override
			protected void doIt() {
				session.doWork(aWork);
			}
		};
	}

	protected abstract Object[] getMergeImmutableEntities();

	protected abstract void mergeUpdateImmutableInDetached(T entity);

	@Test
	@Parameters(method = "getMergeImmutableEntities")
	public void testMergeImmutable(final ID id, final DBUnitAssertionWork aWork) {
		final T entity = new TemplateWorker<T>(dao.getEm()) {
			@Override
			protected void doIt() {
				setResult(dao.findById(id));
			}
		}.getResult();

		mergeUpdateImmutableInDetached(entity);
		new TemplateWorker<T>(dao.getEm()) {
			@Override
			protected void doIt() {
				dao.merge(entity);
			}
		};

		// assert that the immutable properties are not modified
		final Session session = dao.getEm().unwrap(Session.class);
		new TemplateWorker<User>(dao.getEm()) {
			@Override
			protected void doIt() {
				session.doWork(aWork);
			}
		};
	}

}
