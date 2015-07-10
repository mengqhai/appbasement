package com.workstream.rest.model;

import java.util.Date;

import com.workstream.core.model.TaskList;

public class TaskListResponse extends InnerWrapperObj<TaskList> {

	public TaskListResponse(TaskList inner) {
		super(inner);
	}

	public Long getId() {
		return inner.getId();
	}

	public Long getProjectId() {
		return inner.getProject().getId();
	}

	public Long getOrgId() {
		return inner.getOrg().getId();
	}

	public String getName() {
		return inner.getName();
	}

	public String getDescription() {
		return inner.getDescription();
	}

	public Date getStartTime() {
		return inner.getStartTime();
	}

	public Date getDueTime() {
		return inner.getDueTime();
	}

	public boolean isArchived() {
		return inner.isArchived();
	}

}
