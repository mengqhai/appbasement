package com.workstream.rest.model;

import java.util.Date;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class TaskRequest extends MapPropObj {

	public static final String ASSIGNEE = "assignee";
	private static final String DUE_DATE = "dueDate";

	@ApiModelProperty(required = true)
	public void setName(String name) {
		props.put(NAME, name);
	}

	public String getName() {
		return getProp(NAME);
	}

	public void setDescription(String description) {
		props.put(DESCRIPTION, description);
	}

	public String getDescription() {
		return getProp(DESCRIPTION);
	}

	public void setDueDate(Date dueTime) {
		props.put(DUE_DATE, dueTime);
	}

	public Date getDueDate() {
		return getProp(DUE_DATE);
	}

	public void setAssignee(String assignee) {
		props.put(ASSIGNEE, assignee);
	}

	public String getAssignee() {
		return getProp(ASSIGNEE);
	}

	public void setPriority(Integer priority) {
		props.put("priority", priority);
	}

	public Integer getPriority() {
		return getProp("priority");
	}

}
