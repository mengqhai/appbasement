package com.appbasement.persistence;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.*;

import java.util.Map;

import junitparams.JUnitParamsRunner;

import org.hibernate.Session;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.appbasement.model.Template;
import com.appbasement.model.User;
import com.appbasement.persistence.util.DBUnitAssertionWork;
import com.appbasement.persistence.util.EmfHelper;
import com.appbasement.persistence.util.TemplateWorker;
import com.appbasement.persistence.util.TestConstants;
import com.appbasement.persistence.util.UtUtil;

@RunWith(JUnitParamsRunner.class)
public class TemplateJpaDAOTest extends GenericJpaDAOTest<Template, Long> {

	public TemplateJpaDAOTest() {
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
	public GenericJpaDAO<Template, Long> createDao() {
		return new TemplateJpaDAO();
	}

	@Override
	protected Object[] getIdsForRemove() {
		// TODO Auto-generated method stub
		return $($());
	}

	@Override
	protected Object[] getInvalidIdsForRemove() {
		// TODO Auto-generated method stub
		return $($());
	}

	@Override
	protected Object[] getParamEntitiesNotFound() {
		// TODO Auto-generated method stub
		return $($());
	}

	@Override
	protected Object[] getParamEntitiesFound() {
		// TODO Auto-generated method stub
		return $($());
	}

	@Override
	protected Object[] findAllAssertion() {
		// TODO Auto-generated method stub
		return $($());
	}

	@Override
	protected Object[] getPersistEntities() {
		Template template = new Template();
		template.setName("helloTemplate");
		DBUnitAssertionWork aWork = new DBUnitAssertionWork(this.getClass(),
				"testPersist", TestConstants.TABLE_TEMPLATE);

		return $($(template, aWork));
	}

	@Override
	public void testPersist(final Template entity,
			final DBUnitAssertionWork aWork) {
		final TemplateJpaDAO templateDao = (TemplateJpaDAO) dao;
		templateDao.setDefinition(entity, "this is a template");
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
		aWork.replaceLastUpdate(entity.getLastUpdate());

		final Session session = dao.getEm().unwrap(Session.class);
		new TemplateWorker<Template>(dao.getEm()) {
			@Override
			protected void doIt() {
				session.doWork(aWork);
			}
		};
	}

	@Override
	protected Object[] getPersistEntitiesInvalid() {
		// TODO Auto-generated method stub
		return $($());
	}

	@Override
	protected Object[] getMergeEntities() {
		return $($());
	}

	@Override
	protected void mergeUpdateInDetached(Template entity,
			Map<String, Object> modifiedAtts) {
	}

	@Override
	@Ignore
	@Test
	/**
	 * Template doesn't support to be modfied in detached state.
	 * 
	 */
	public void testMerge(final Long id, final DBUnitAssertionWork aWork) {
	}

	@Override
	protected Object[] getMergeImmutableEntities() {
		return $($());
	}

	@Override
	protected void mergeUpdateImmutableInDetached(Template entity) {
	}

	@Test
	public void testSetGetDefinition() {
		final long id = 1l;
		final ITemplateDAO tempDao = (ITemplateDAO) dao;
		String initDefinition = new TemplateWorker<String>(dao.getEm()) {
			@Override
			protected void doIt() {
				Template template = tempDao.findById(id);
				String definition = tempDao.getDefinition(template);
				setResult(definition);
			}
		}.getResult();
		assertEquals(
				"<html><body><img src='cid:spitterLogo'><h4>${username} says...</h4><i>${words}</i></body></html>",
				initDefinition);

		Template initTemplate = new TemplateWorker<Template>(dao.getEm()) {
			@Override
			protected void doIt() {
				Template template = tempDao.findById(id);
				setResult(template);
			}
		}.getResult();

		dao.getEm().detach(initTemplate);

		final String updateTo = "modified definition";
		final Template updated = new TemplateWorker<Template>(dao.getEm()) {
			@Override
			protected void doIt() {
				Template template = tempDao.findById(id);
				template.setName("testModfiedTemplate");
				tempDao.setDefinition(template, updateTo);
				setResult(template);
			}
		}.getResult();

		dao.getEm().detach(updateTo);

		assertTrue(updated.getLastUpdate().getTime() > initTemplate
				.getLastUpdate().getTime());

		new TemplateWorker<Template>(dao.getEm()) {
			@Override
			protected void doIt() {
				Template template = tempDao.findById(id);
				String definition = tempDao.getDefinition(template);
				assertEquals(updateTo, definition);
				assertEquals(updated.getName(), template.getName());
			}
		};

		final DBUnitAssertionWork aWork = new DBUnitAssertionWork(
				this.getClass(), "setGetDefinition",
				TestConstants.TABLE_TEMPLATE, "id =" + id);
		aWork.replaceId(updated.getId());
		aWork.replaceLastUpdate(updated.getLastUpdate());

		final Session session = dao.getEm().unwrap(Session.class);
		new TemplateWorker<User>(dao.getEm()) {
			@Override
			protected void doIt() {
				session.doWork(aWork);
			}
		};
	}

}
