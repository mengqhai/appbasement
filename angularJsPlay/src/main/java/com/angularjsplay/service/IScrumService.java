package com.angularjsplay.service;

import java.util.Collection;

import com.angularjsplay.model.Backlog;
import com.angularjsplay.model.Sprint;
import com.angularjsplay.model.Task;
import com.appbasement.service.crud.ICrudService;

public interface IScrumService extends ICrudService{

	public abstract Collection<Backlog> getAllBacklogsForProject(Long projectId);

	public abstract Collection<Sprint> getAllSprintsForProject(Long projectId);

	public abstract Collection<Backlog> getBacklogsForProject(Long projectId,
			int first, int max);

	public abstract Long getBacklogCountForProject(Long projectId);

	public abstract Long getSprintCountForProject(Long projectId);

	public abstract Collection<Sprint> getSprintsForProject(Long projectId,
			int first, int max);

	public abstract Long getBacklogCountForSprint(Long sprintId);

	public abstract Collection<Backlog> getBacklogsForSprint(Long sprintId, int first,
			int max);

	public abstract Collection<Backlog> getAllBacklogsForSprint(Long sprintId);

	public abstract Long getTaskCountForBacklog(Long backlogId);

	public abstract Collection<Task> getTasksForBacklog(Long backlogId, int first,
			int max);

	public abstract Collection<Task> getAllTasksForBacklog(Long backlogId);

}
