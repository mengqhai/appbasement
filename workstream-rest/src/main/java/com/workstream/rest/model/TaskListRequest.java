package com.workstream.rest.model;

import java.util.Date;

public class TaskListRequest extends MapPropObj {

	public void setName(String name) {
		this.props.put(NAME, name);
	}

	public String getName() {
		return this.getProp(NAME);
	}

	public void setDescription(String description) {
		this.props.put(DESCRIPTION, description);
	}

	public String getDescription() {
		return this.getProp(DESCRIPTION);
	}

	public void setStartTime(Date startTime) {
		this.props.put("startTime", startTime);
	}

	public Date getStartTime() {
		return this.getProp("startTime");
	}

	public void setDueTime(Date dueTime) {
		this.props.put("dueTime", dueTime);
	}

	public Date getDueTime() {
		return this.getProp("dueTime");
	}
}
