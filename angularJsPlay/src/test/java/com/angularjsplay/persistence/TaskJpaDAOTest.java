package com.angularjsplay.persistence;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Map;

import junitparams.JUnitParamsRunner;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.angularjsplay.model.Task;
import com.angularjsplay.model.Task.TaskState;
import com.angularjsplay.persistence.util.ScrumTestConstants;
import com.appbasement.persistence.GenericJpaDAO;
import com.appbasement.persistence.GenericJpaDAOTest;
import com.appbasement.persistence.util.DBUnitAssertionWork;
import com.appbasement.persistence.util.DBUnitHelper;
import com.appbasement.persistence.util.EmfHelper;
import com.appbasement.persistence.util.TestConstants;

@RunWith(JUnitParamsRunner.class)
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

	@Before
	public void setUp() throws Exception {
		super.setUp();
		DBUnitHelper.importDataSet(EmfHelper.getEmf(),
				ScrumTestConstants.DATA_SET_SMALL_PROJECT,
				ScrumTestConstants.DATA_SET_SMALL_BACKLOG,
				ScrumTestConstants.DATA_SET_SMALL_TASK);
	}

	@Override
	public GenericJpaDAO<Task, Long> createDao() {
		return new TaskJpaDAO();
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
		Task task = new Task();
		task.setName("Team task #1");
		task.setDesc("This is a test task.");
		task.setEstimation((short) 80);
		task.setRemaining((short) 40);
		task.setCreatedAt(new Date());
		task.setState(TaskState.IN_PROGRESS);

		DBUnitAssertionWork aWork = new DBUnitAssertionWork(this.getClass(),
				"testPersist", ScrumTestConstants.TABLE_TASK).replaceNull()
				.replaceCreatedAt(task.getCreatedAt());

		return $($(task, aWork));
	}

	@Override
	protected Object[] getPersistEntitiesInvalid() {
		// TODO Auto-generated method stub
		return $($());
	}

	@Override
	protected Object[] getMergeEntities() {
		final DBUnitAssertionWork aWork = new DBUnitAssertionWork(
				this.getClass(), "testMerge", TestConstants.TABLE_USER,
				"id in (1)");
		return $($(1l, aWork));
	}

	@Override
	protected void mergeUpdateInDetached(Task entity,
			Map<String, Object> modifiedAtts) {
		switch (entity.getId().toString()) {
		case "1":
			String name = "Hello task 1";
			String desc = "Description of task 1";
			entity.setName(name);
			
		}
	}

	@Override
	protected Object[] getMergeImmutableEntities() {
		// TODO Auto-generated method stub
		return $($());
	}

	@Override
	protected void mergeUpdateImmutableInDetached(Task entity) {
		// TODO Auto-generated method stub

	}

}
