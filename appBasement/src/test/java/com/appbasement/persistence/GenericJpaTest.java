package com.appbasement.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;

import com.appbasement.model.User;
import com.appbasement.persistence.util.DBUnitAssertionWork;
import com.appbasement.persistence.util.DBUnitHelper;
import com.appbasement.persistence.util.EmfHelper;
import com.appbasement.persistence.util.TemplateWorker;
import com.appbasement.persistence.util.UtUtil;

public abstract class GenericJpaTest<T, ID extends Serializable> {

	protected GenericJpaDAO<T, ID> dao = null;

	public abstract GenericJpaDAO<T, ID> createDao();

	public void setUp() throws Exception {
		DBUnitHelper.importDataSet(EmfHelper.getEmf());
		dao = createDao();
		dao.setEm(EmfHelper.getEmf().createEntityManager());
	}

	public void tearDown() throws Exception {
		dao.getEm().close();
	}

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

	public void testRemoveInvalid(final ID id) {
		final T entity = dao.getReference(id);
		new TemplateWorker<T>(dao.getEm()) {
			@Override
			protected void doIt() {
				dao.remove(entity);
			}
		};
	}

	public void testRemoveNull() {
		new TemplateWorker<T>(dao.getEm()) {
			@Override
			protected void doIt() {
				dao.remove(null);
			}
		};
	}

	public void testFindByIDNull() {
		new TemplateWorker<T>(dao.getEm()) {
			@Override
			protected void doIt() {
				dao.findById(null);
			}
		};
	}

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

	public void testGetReferenceNull(final ID id, final T expected) {
		new TemplateWorker<T>(dao.getEm()) {
			@Override
			protected void doIt() {
				T actual = dao.getReference(id);
				Hibernate.initialize(actual);
			}
		};
	}

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

	public void testInvalidEntity(final T invalidEntity) {
		new TemplateWorker<T>(dao.getEm()) {
			@Override
			protected void doIt() {
				dao.persist(invalidEntity);
			}
		};
	}

	public void testPersistNullEntity() {
		new TemplateWorker<T>(dao.getEm()) {
			@Override
			protected void doIt() {
				dao.persist(null);
			}
		};
	}

}
