package com.appbasement.persistence;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.hibernate.Session;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.appbasement.model.Template;
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
		return $($(1l), $(2l));
	}

	@Override
	protected Object[] getInvalidIdsForRemove() {
		return $($(0l), $(-1l), $(1000l));
	}

	@Override
	protected Object[] getParamEntitiesNotFound() {
		return $($(0l, null), $(-1l, null), $(1000l, null));
	}

	@Override
	protected Object[] getParamEntitiesFound() {
		return $(
				$(1l,
						new Template().setName("testTemplate").setLastUpdate(
								Timestamp.valueOf("2013-03-01 22:26:26"))),
				$(2l,
						new Template().setName("testTemplate2").setLastUpdate(
								Timestamp.valueOf("2013-02-01 22:30:26"))));
	}

	@Override
	protected Object[] findAllAssertion() {
		int resultListSize = 2;
		Collection<Template> mustContain = new ArrayList<Template>();
		Object[] objArr = getParamEntitiesFound();
		for (Object obj : objArr) {
			Object[] entry = (Object[]) obj;
			mustContain.add((Template) entry[1]);
		}
		return $($(resultListSize, mustContain));
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
		return $($(new Template()));
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
	protected Object[] getMergeImmutableEntities() {
		return $($());
	}

	@Override
	protected void mergeUpdateImmutableInDetached(Template entity) {
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testMergeNotSupport() {
		dao.merge(new Template());
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
		new TemplateWorker<Template>(dao.getEm()) {
			@Override
			protected void doIt() {
				session.doWork(aWork);
			}
		};
	}

	@Test(expected = IllegalStateException.class)
	public void testSetGetDefinitionIllegalState() {
		final ITemplateDAO tDao = (ITemplateDAO) dao;
		final Template dTemplate = new TemplateWorker<Template>(dao.getEm()) {
			@Override
			protected void doIt() {
				setResult(dao.findById(1l));
			}
		}.getResult();
		// Force detach
		dao.getEm().detach(dTemplate);
		new TemplateWorker<Template>(dao.getEm()) {
			@Override
			protected void doIt() {
				tDao.setDefinition(dTemplate, "fail");
			}
		};
	}

	protected Object[] getSetDefinitionInvalidArguments() {
		return $($(null, "some"), $(new Template().setName("hello"), null));
	}

	@Test(expected = IllegalArgumentException.class)
	@Parameters(method = "getSetDefinitionInvalidArguments")
	public void testSetDefinitionInvalidArguments(Template template,
			String definition) {
		final ITemplateDAO tDao = (ITemplateDAO) dao;
		tDao.setDefinition(template, definition);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetDefinitionInvalidArguments() {
		final ITemplateDAO tDao = (ITemplateDAO) dao;
		tDao.getDefinition(null);
	}

	protected Object[] getTemplateFindByName() {
		List<Object[]> params = new ArrayList<Object[]>();

		Object[] objArr = getParamEntitiesFound();
		for (Object obj : objArr) {
			Object[] entry = (Object[]) obj;
			Template template = (Template) entry[1];
			params.add($(template.getName(), template));
		}

		return params.toArray();
	}

	@Test
	@Parameters(method = "getTemplateFindByName")
	public void testFindTemplateByName(final String name,
			final Template expected) {
		final ITemplateDAO tDao = (ITemplateDAO) dao;
		new TemplateWorker<Template>(dao.getEm()) {
			@Override
			protected void doIt() {
				Template template = tDao.findByName(name);
				assertEquals(expected, template);
			}
		};
	}

	@Test
	public void testFindTemplateByNameNotFound() {
		final ITemplateDAO tDao = (ITemplateDAO) dao;
		new TemplateWorker<Template>(dao.getEm()) {
			@Override
			protected void doIt() {
				Template template = tDao.findByName("NoSuchTemplate");
				assertEquals(null, template);
			}
		};
	}

	protected Object[] getLastUpdateParams() {
		return $($("testTemplate", Timestamp.valueOf("2013-03-01 22:26:26")),
				$("testTemplate2", Timestamp.valueOf("2013-02-01 22:30:26")));
	}

	@Test
	@Parameters(method = "getLastUpdateParams")
	public void testGetLastUpdate(final String name, final Date time) {
		final ITemplateDAO tDao = (ITemplateDAO) dao;
		new TemplateWorker<Template>(dao.getEm()) {
			@Override
			protected void doIt() {
				assertEquals(time.getTime(), tDao.getLastUpdate(name).getTime());
			}
		};
	}

}
