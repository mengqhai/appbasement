package com.angularjsplay.persistence;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.angularjsplay.model.Task;
import com.appbasement.persistence.GenericJpaDAO;
import com.appbasement.persistence.GenericJpaDAOTest;
import com.appbasement.persistence.util.EmfHelper;

public class TaskJpaDAOTest extends GenericJpaDAOTest<Task, Long> {
	
	@BeforeClass
	public static void setUpBeforeClass() {
		EmfHelper.initEmf();
		assertNotNull(EmfHelper.getEmf());
		assertTrue(EmfHelper.getEmf().isOpen());
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		EmfHelper.closeEmf();
	}


	@Override
	public GenericJpaDAO<Task, Long> createDao() {
		return new TaskJpaDAO();
	}
	

	@Override
	protected Object[] getIdsForRemove() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object[] getInvalidIdsForRemove() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object[] getParamEntitiesNotFound() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object[] getParamEntitiesFound() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object[] findAllAssertion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object[] getPersistEntities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object[] getPersistEntitiesInvalid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object[] getMergeEntities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void mergeUpdateInDetached(Task entity,
			Map<String, Object> modifiedAtts) {
		// TODO Auto-generated method stub

	}

	@Override
	protected Object[] getMergeImmutableEntities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void mergeUpdateImmutableInDetached(Task entity) {
		// TODO Auto-generated method stub

	}

}
