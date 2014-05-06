package com.angularjsplay.service;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import com.angularjsplay.exception.ScrumResourceNotFoundException;
import com.angularjsplay.exception.ScrumValidationException;
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
import com.appbasement.persistence.IGenericDAO;

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
	IObjectPatcher objectPatcher;

	private Map<String, IGenericDAO<? extends IEntity, Long>> daoMap = new HashMap<String, IGenericDAO<? extends IEntity, Long>>();

	public ScrumService() {
	}

	@PostConstruct
	public void init() {
		daoMap.put(Project.class.getName(), projectDao);
		daoMap.put(Backlog.class.getName(), bDao);
		daoMap.put(Sprint.class.getName(), sDao);
		daoMap.put(Task.class.getName(), tDao);
	}

	@SuppressWarnings("unchecked")
	protected <T extends IEntity> IGenericDAO<T, Long> getDao(
			Class<? extends IEntity> type) {
		IGenericDAO<T, Long> dao = (IGenericDAO<T, Long>) daoMap.get(type
				.getName());
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

	private void changeRelationshipsForBacklog(Backlog backlog,
			Long newProjectId, Long newSprintId) {
		if (newProjectId != null) {
			// backlog.project is changed
			Project project = projectDao.getReference(newProjectId);
			project.addBacklogToProject(backlog);
		}
		if (newSprintId != null) {
			// backlog.sprint is changed
			Sprint sprint = sDao.getReference(newSprintId);
			if (!sprint.getProjectId().equals(backlog.getProjectId())) {
				throw new ScrumValidationException("Sprint " + sprint.getId()
						+ " doesn't belong to project " + newProjectId);
			}
			sprint.addBacklogToSprint(backlog);
		}
	}

	private void changeRelationshipsForSprint(Sprint sprint, Long newProjectId) {
		if (newProjectId != null) {
			// sprint.project is changed
			Project project = projectDao.getReference(newProjectId);
			project.addSprintToProject(sprint);
		}
	}

	@Override
	public void createBacklogWithPartialRelationships(Backlog backlog) {
		if (backlog.getProject() == null) {
			throw new IllegalArgumentException("Null project in backlog");
		}
		Long projectId = backlog.getProject().getId();
		if (projectId == null) {
			throw new IllegalArgumentException("Null id in sprint.project");
		}
		Long sprintId = backlog.getSprintId();
		try {
			changeRelationshipsForBacklog(backlog, projectId, sprintId);
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
		Backlog backlog = getById(Backlog.class, patch.getId());
		Map<Field, PatchedValue> patchResult = objectPatcher.patchObject(
				backlog, patch);
		if (!patchResult.isEmpty()) {
			try {
				Long projectId = patch.getProjectId();
				Long sprintId = patch.getSprintId();
				changeRelationshipsForBacklog(backlog, projectId, sprintId);
				// unset sprintId
				if (patch.isRemoveSprint()) {
					backlog.setSprintId(null);
				}
				save(backlog);
			} catch (EntityNotFoundException e) {
				throw new ScrumResourceNotFoundException();
			}
		} else if (patch.isRemoveSprint()) {
			backlog.setSprintId(null);
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
			changeRelationshipsForSprint(sprint, projectId);
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
		Sprint sprint = getById(Sprint.class, patch.getId());
		Map<Field, PatchedValue> patchResult = objectPatcher.patchObject(
				sprint, patch);
		if (!patchResult.isEmpty()) {
			Long projectId = patch.getProjectId();
			try {
				changeRelationshipsForSprint(sprint, projectId);
				save(sprint);
			} catch (EntityNotFoundException e) {
				throw new ScrumResourceNotFoundException();
			}
		}
	}

}
