package com.workstream.core.persistence;

import java.util.Collection;

import com.workstream.core.model.Project;
import com.workstream.core.model.TaskList;

public interface ITaskListDAO extends IGenericDAO<TaskList, Long> {

	public abstract Collection<TaskList> filterFor(Project project, int first, int max);

}
