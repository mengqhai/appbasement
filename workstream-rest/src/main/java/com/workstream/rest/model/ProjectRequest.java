package com.workstream.rest.model;

import java.util.Date;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel
public class ProjectRequest extends MapPropObj {

	public static final String DUE_TIME = "dueTime";
	public static final String START_TIME = "startTime";

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

	public void setStartTime(Date startTime) {
		props.put(START_TIME, startTime);
	}

	public Date getStartTime() {
		return getProp(START_TIME);
	}

	public void setDueTime(Date dueTime) {
		props.put(DUE_TIME, dueTime);
	}

	public Date getDueTime() {
		return getProp(DUE_TIME);
	}

}
