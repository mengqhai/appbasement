package com.angularjsplay.service;

import java.util.Collection;
import java.util.List;

import com.angularjsplay.exception.ScrumResourceNotFoundException;
import com.angularjsplay.model.Backlog;
import com.angularjsplay.model.IEntity;
import com.angularjsplay.model.Sprint;

public interface IScrumService {

	public abstract <T extends IEntity> void deleteById(Class<T> type, Long id)
			throws ScrumResourceNotFoundException;

	public abstract <T extends IEntity> List<T> getAll(Class<T> type);

	public abstract <T extends IEntity> T getById(Class<T> type, Long id)
			throws ScrumResourceNotFoundException;

	public abstract <T extends IEntity> void save(T entity);

	public abstract Collection<Backlog> getAllBacklogsForProject(Long projectId);

	public abstract Collection<Sprint> getAllSprintsForProject(Long projectId);

	public abstract Collection<Backlog> getBacklogsForProject(Long projectId, int first,
			int max);

	public abstract Long getBacklogCountForProject(Long projectId);

	public abstract Long getSprintCountForProject(Long projectId);

	public abstract Collection<Sprint> getSprintsForProject(Long projectId, int first,
			int max);

}
