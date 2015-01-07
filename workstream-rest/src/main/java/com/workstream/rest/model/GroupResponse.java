package com.workstream.rest.model;

import java.util.Date;

import org.activiti.engine.identity.Group;

import com.workstream.core.model.GroupX;

public class GroupResponse {

	private Group group;
	private GroupX groupX;

	public GroupResponse(Group group, GroupX groupX) {
		this.group = group;
		this.groupX = groupX;
	}

	public GroupResponse(Group group) {
		this.group = group;
	}

	public String getGroupId() {
		return group.getId();
	}

	public String getName() {
		return group.getName();
	}

	public String getType() {
		return group.getType();
	}

	public String getDescription() {
		if (groupX == null) {
			return null;
		}
		return groupX.getDescription();
	}

	public Date getCreatedAt() {
		if (groupX == null) {
			return null;
		}
		return groupX.getCreatedAt();
	}

}
