package com.angularjsplay.persistence;

import java.util.Collection;

import com.angularjsplay.model.Task;
import com.appbasement.persistence.IGenericDAO;

public interface ITaskDAO extends IGenericDAO<Task, Long> {

	public abstract Collection<Task> getTasksForBacklog(Long backlogId, int first,
			int max);

	public abstract Collection<Task> getTasksForBacklog(Long backlogId);

	public abstract Long getTaskCountForBacklog(Long backlogId);

}
