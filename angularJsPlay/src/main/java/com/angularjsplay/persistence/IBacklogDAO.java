package com.angularjsplay.persistence;

import java.util.Collection;

import com.angularjsplay.model.Backlog;
import com.appbasement.persistence.IGenericDAO;

public interface IBacklogDAO extends IGenericDAO<Backlog, Long> {

	public abstract Collection<Backlog> getBacklogsForProject(Long projectId, int first,
			int max);

	public abstract Collection<Backlog> getBacklogsForProject(Long projectId);

	public abstract Long getBacklogCountForProject(Long projectId);

	public abstract Long getBacklogCountForSprint(Long sprintId);

	public abstract Collection<Backlog> getBacklogsForSprint(Long sprintId, int first,
			int max);

	public abstract Collection<Backlog> getBacklogsForSprint(Long sprintId);

}
