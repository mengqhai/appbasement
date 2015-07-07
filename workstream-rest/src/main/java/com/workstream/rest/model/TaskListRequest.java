package com.workstream.rest.model;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.workstream.rest.RestConstants;
import com.workstream.rest.validation.ValidateOnCreate;

public class TaskListRequest extends MapPropObj {

	public void setName(String name) {
		this.props.put(NAME, name);
	}

	@NotNull(groups = ValidateOnCreate.class)
	@Size(max = RestConstants.VALID_NAME_SIZE, min = 1)
	public String getName() {
		return this.getProp(NAME);
	}

	public void setDescription(String description) {
		this.props.put(DESCRIPTION, description);
	}

	@Size(max = RestConstants.VALID_DESCRIPTION_SIZE, min = 1)
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
