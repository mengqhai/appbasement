package com.workstream.rest.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.workstream.core.model.ProjectMembership.ProjectMembershipType;

public class ProjectMembershipRequest extends MapPropObj {

	public void setUserId(String userId) {
		props.put("userId", userId);
	}

	public String getUserId() {
		return getProp("userId");
	}

	@NotNull
	@Size(min = 1)
	public ProjectMembershipType getType() {
		return getProp("type");
	}

	public void setType(ProjectMembershipType type) {
		props.put("type", type);
	}
}
