package com.workstream.rest.model;

import com.workstream.core.model.ProjectMembership.ProjectMembershipType;

public class ProjectMembershipRequest extends MapPropObj {

	public void setUserId(String userId) {
		props.put("userId", userId);
	}

	public String getUserId() {
		return getProp("userId");
	}

	public ProjectMembershipType getType() {
		return getProp("type");
	}

	public void setType(ProjectMembershipType type) {
		props.put("type", type);
	}
}
