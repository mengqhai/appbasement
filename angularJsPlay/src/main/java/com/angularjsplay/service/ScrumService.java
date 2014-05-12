package com.angularjsplay.service;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import com.angularjsplay.exception.ScrumResourceNotFoundException;
import com.angularjsplay.model.Backlog;
import com.angularjsplay.model.IEntity;
import com.angularjsplay.model.Project;
import com.angularjsplay.model.Sprint;
import com.angularjsplay.model.Task;
import com.angularjsplay.persistence.IBacklogDAO;
import com.angularjsplay.persistence.IProjectDAO;
import com.angularjsplay.persistence.ISprintDAO;
import com.angularjsplay.persistence.ITaskDAO;
import com.appbasement.component.IObjectPatcher;
import com.appbasement.component.PatchedValue;
import com.appbasement.component.beanprocessor.IBeanProcessorManager;
import com.appbasement.persistence.IDaoRegistry;
import com.appbasement.persistence.IGenericDAO;
import com.appbasement.persistence.IUserDAO;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class ScrumService implements IScrumService {

	@Autowired
	protected IProjectDAO projectDao;

	@Autowired
	protected IBacklogDAO bDao;

	@Autowired
	protected ISprintDAO sDao;

	@Autowired
	protected ITaskDAO tDao;

	@Autowired
	protected IUserDAO uDao;

	@Autowired
	protected IDaoRegistry daoReg;

	@Autowired
	protected IBeanProcessorManager bMgr;

	@Autowired
	@Qualifier("strategyEnabledObjectPatcher")
	IObjectPatcher objectPatcher;

	public ScrumService() {
	}

	@SuppressWarnings("unchecked")
	protected <T extends IEntity> IGenericDAO<T, Long> getDao(
			Class<? extends IEntity> type) {
		IGenericDAO<T, Long> dao = (IGenericDAO<T, Long>) daoReg.getDao(type);

		if (dao == null) {
			throw new IllegalArgumentException(
					"Scrum service is not capable to handle type "
							+ type.getName());
		}
		return dao;
	}

	@Override
	public <T extends IEntity> void save(T entity) {
		IGenericDAO<T, Long> dao = getDao(entity.getClass());
		if (entity.getId() == null) {
			dao.persist((T) entity);
		} else {
			dao.merge((T) entity);
		}
	}

	@Override
	public <T extends IEntity> T getById(Class<T> type, Long id)
			throws ScrumResourceNotFoundException {
		IGenericDAO<T, Long> dao = getDao(type);
		T entity = dao.findById(id);
		if (entity == null) {
			throw new ScrumResourceNotFoundException();
		} else {
			return entity;
		}
	}

	@Override
	public <T extends IEntity> T getById(Class<T> type, Long id,
			String... eagerFields) {
		final T entity = getById(type, id);
		if (entity == null) {
			throw new ScrumResourceNotFoundException();
		}

		for (String fieldName : eagerFields) {
			Field field = ReflectionUtils.findField(type, fieldName);
			field.setAccessible(true);
			Object proxy = ReflectionUtils.getField(field, entity);
			if (proxy == null) {
				continue;
			}
			projectDao.initialize(proxy);
		}
		return entity;
	}

	@Override
	public <T extends IEntity> List<T> getAll(Class<T> type) {
		IGenericDAO<T, Long> dao = getDao(type);
		return dao.findAll();
	}

	@Override
	public <T extends IEntity> void deleteById(Class<T> type, Long id)
			throws ScrumResourceNotFoundException {
		IGenericDAO<T, Long> dao = getDao(type);
		T toDelete = dao.getReference(id);

		if (toDelete instanceof Sprint) {
			// unset sprintId
			bDao.unsetSprintOfBacklogsForSprint(toDelete.getId());
		}

		try {
			dao.remove(toDelete);
		} catch (EntityNotFoundException e) {
			throw new ScrumResourceNotFoundException(e);
		}
	}

	@Override
	public Collection<Backlog> getAllBacklogsForProject(Long projectId) {
		return bDao.getBacklogsForProject(projectId);
	}

	@Override
	public Collection<Backlog> getBacklogsForProject(Long projectId, int first,
			int max) {
		return bDao.getBacklogsForProject(projectId, first, max);
	}

	@Override
	public Long getBacklogCountForProject(Long projectId) {
		return bDao.getBacklogCountForProject(projectId);
	}

	@Override
	public Collection<Sprint> getAllSprintsForProject(Long projectId) {
		return sDao.getSprintsForProject(projectId);
	}

	@Override
	public Collection<Sprint> getSprintsForProject(Long projectId, int first,
			int max) {
		return sDao.getSprintsForProject(projectId, first, max);
	}

	@Override
	public Long getSprintCountForProject(Long projectId) {
		return sDao.getSprintCountForProject(projectId);
	}

	@Override
	public Collection<Backlog> getAllBacklogsForSprint(Long sprintId) {
		return bDao.getBacklogsForSprint(sprintId);
	}

	@Override
	public Collection<Backlog> getBacklogsForSprint(Long sprintId, int first,
			int max) {
		return bDao.getBacklogsForSprint(sprintId, first, max);
	}

	@Override
	public Long getBacklogCountForSprint(Long sprintId) {
		return bDao.getBacklogCountForSprint(sprintId);
	}

	public void createBacklogWithPartialRelationships(Backlog backlog) {
		if (backlog.getProject() == null) {
			throw new IllegalArgumentException("Null project in backlog");
		}
		Long projectId = backlog.getProject().getId();
		if (projectId == null) {
			throw new IllegalArgumentException("Null id in sprint.project");
		}
		try {
			objectPatcher.patchObject(backlog, backlog);
			bMgr.doBeforeCreate(backlog);
			save(backlog);
		} catch (EntityNotFoundException e) {
			throw new ScrumResourceNotFoundException();
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateBacklogWithPatch(Backlog patch) {
		if (patch.getId() == null) {
			throw new IllegalArgumentException("Null id in patch");
		}
		try {
			Backlog backlog = getById(Backlog.class, patch.getId());
			Map<Field, PatchedValue> patchResult = objectPatcher.patchObject(
					backlog, patch);
			if (!patchResult.isEmpty() || patch.isRemoveSprint()) {
				bMgr.doBeforeUpdateWithPatch(backlog, patch);
				save(backlog);
			}
		} catch (EntityNotFoundException e) {
			throw new ScrumResourceNotFoundException();
		}
	}

	@Override
	public void createSprintWithPartialRelationships(Sprint sprint) {
		if (sprint.getProject() == null) {
			throw new IllegalArgumentException("Null project in sprint");
		}
		Long projectId = sprint.getProject().getId();
		if (projectId == null) {
			throw new IllegalArgumentException("Null id in sprint.project");
		}
		try {
			objectPatcher.patchObject(sprint, sprint);
			bMgr.doBeforeCreate(sprint);
			save(sprint);
		} catch (EntityNotFoundException e) {
			throw new ScrumResourceNotFoundException();
		}
	}

	@Override
	public void updateSprintWithPatch(Sprint patch) {
		if (patch.getId() == null) {
			throw new IllegalArgumentException("Null id in patch");
		}
		try {
			Sprint sprint = getById(Sprint.class, patch.getId());
			Map<Field, PatchedValue> patchResult = objectPatcher.patchObject(
					sprint, patch);
			if (!patchResult.isEmpty()) {
				bMgr.doBeforeUpdateWithPatch(sprint, patch);
				save(sprint);
			}
		} catch (EntityNotFoundException e) {
			throw new ScrumResourceNotFoundException();
		}
	}

	@Override
	public void updateProjectWithPatch(Project patch) {
		if (patch.getId() == null) {
			throw new IllegalArgumentException("Null id in patch");
		}
		try {
			Project existing = getById(Project.class, patch.getId());
			Map<Field, PatchedValue> patchedResult = objectPatcher.patchObject(
					existing, patch);
			if (!patchedResult.isEmpty()) {
				bMgr.doBeforeUpdateWithPatch(existing, patch);
				save(existing);
			}
		} catch (EntityNotFoundException e) {
			throw new ScrumResourceNotFoundException();
		}

	}

	@Override
	public void createTaskWithPartialRelationships(Task task) {
		try {
			objectPatcher.patchObject(task, task);
			bMgr.doBeforeCreate(task);
			save(task);
		} catch (EntityNotFoundException e) {
			throw new ScrumResourceNotFoundException();
		}
	}

	@Override
	public void updateTaskWithPatch(Task patch) {
		if (patch.getId() == null) {
			throw new IllegalArgumentException("Null id in patch");
		}
		try {
			Task existing = getById(Task.class, patch.getId());
			Map<Field, PatchedValue> patchedResult = objectPatcher.patchObject(
					existing, patch);
			if (!patchedResult.isEmpty()) {
				bMgr.doBeforeUpdateWithPatch(existing, patch);
				save(existing);
			}
		} catch (EntityNotFoundException e) {
			throw new ScrumResourceNotFoundException();
		}
	}
}
