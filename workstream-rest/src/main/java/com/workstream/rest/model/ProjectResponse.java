package com.workstream.rest.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.workstream.core.model.Project;

@JsonInclude(Include.NON_NULL)
public class ProjectResponse {

	private Project project;

	public ProjectResponse(Project project) {
		this.project = project;
	}

	public Long getId() {
		return project.getId();
	}

	public String getName() {
		return project.getName();
	}

	public String getDescription() {
		return project.getDescription();
	}

	public Date getStartTime() {
		return project.getStartTime();
	}

	public Date getDueTime() {
		return project.getDueTime();
	}

	public Long getOrgId() {
		return project.getOrg().getId();
	}

	public Date getCreatedAt() {
		return project.getCreatedAt();
	}

}
