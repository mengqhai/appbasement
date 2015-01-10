package com.workstream.rest.model;

import java.util.Date;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel
public class ProjectRequest extends MapPropObj {

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

	@ApiModelProperty(dataType = "date")
	public void setStartTime(Date startTime) {
		props.put("startTime", startTime);
	}

	public Date getStartTime() {
		return getProp("startTime");
	}

	@ApiModelProperty(dataType = "date")
	public void setDueTime(Date dueTime) {
		props.put("dueTime", dueTime);
	}

	public Date getDueTime() {
		return getProp("dueTime");
	}

}
