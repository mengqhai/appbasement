package com.appbasement.persistence;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import junitparams.JUnitParamsRunner;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.appbasement.model.Template;
import com.appbasement.persistence.util.EmfHelper;

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
		// TODO Auto-generated method stub
		return $($());
	}

	@Override
	protected Object[] getPersistEntitiesInvalid() {
		// TODO Auto-generated method stub
		return $($());
	}

	@Override
	protected Object[] getMergeEntities() {
		// TODO Auto-generated method stub
		return $($());
	}

	@Override
	public void mergeUpdateInDetached(Template entity,
			Map<String, Object> modifiedAtts) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Object[] getMergeImmutableEntities() {
		// TODO Auto-generated method stub
		return $($());
	}

	@Override
	protected void mergeUpdateImmutableInDetached(Template entity) {
		// TODO Auto-generated method stub
		
	}

}
