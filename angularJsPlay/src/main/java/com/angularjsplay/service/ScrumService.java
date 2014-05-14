package com.angularjsplay.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.angularjsplay.model.Backlog;
import com.angularjsplay.model.Sprint;
import com.angularjsplay.model.Task;
import com.angularjsplay.persistence.IBacklogDAO;
import com.angularjsplay.persistence.ISprintDAO;
import com.angularjsplay.persistence.ITaskDAO;
import com.appbasement.exception.ResourceNotFoundException;
import com.appbasement.service.crud.ICrudService;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class ScrumService implements IScrumService {

	@Autowired
	protected IBacklogDAO bDao;

	@Autowired
	protected ISprintDAO sDao;

	@Autowired
	protected ITaskDAO tDao;

	@Autowired
	ICrudService crud;

	public ScrumService() {
	}

	@Override
	public <T> void deleteById(Class<T> type, Serializable id)
			throws ResourceNotFoundException {
		crud.deleteById(type, id);
	}

	@Override
	public <T> List<T> getAll(Class<T> type) {
		return crud.getAll(type);
	}

	@Override
	public <T> T getById(Class<T> type, Serializable id)
			throws ResourceNotFoundException {
		return crud.getById(type, id);
	}

	@Override
	public <T> void updateWithPatch(T patch) {
		crud.updateWithPatch(patch);
	}

	@Override
	public <T> void createWithIdRef(T entity) {
		crud.createWithIdRef(entity);
	}

	@Override
	public <T> T getById(Class<T> type, Serializable id, String... eagerFields) {
		return crud.getById(type, id, eagerFields);
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

	@Override
	public Collection<Task> getAllTasksForSprint(Long backlogId) {
		return tDao.getTasksForBacklog(backlogId);
	}

	@Override
	public Collection<Task> getTasksForSprint(Long backlogId, int first,
			int max) {
		return tDao.getTasksForSprint(backlogId, first, max);
	}

	@Override
	public Long getTaskCountForSprint(Long backlogId) {
		return tDao.getTaskCountForSprint(backlogId);
	}
}
