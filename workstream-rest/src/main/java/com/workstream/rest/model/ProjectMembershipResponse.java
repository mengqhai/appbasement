package com.workstream.rest.model;

import com.workstream.core.model.ProjectMembership;
import com.workstream.core.model.ProjectMembership.ProjectMembershipType;

public class ProjectMembershipResponse extends
		InnerWrapperObj<ProjectMembership> {
	public ProjectMembershipResponse(ProjectMembership membership) {
		super(membership);
	}

	public Long getId() {
		return inner.getId();
	}

	public String getUserId() {
		return inner.getUserId();
	}

	public ProjectMembershipType getType() {
		return inner.getType();
	}

	public Long getProjectId() {
		return inner.getProject().getId();
	}

	public Long getOrgId() {
		return inner.getOrg().getId();
	}

}
