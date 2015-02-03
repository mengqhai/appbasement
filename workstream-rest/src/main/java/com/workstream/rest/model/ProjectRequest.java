package com.workstream.rest.model;

import java.util.Date;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import com.workstream.rest.RestConstants;
import com.workstream.rest.validation.ValidateOnCreate;

@ApiModel
public class ProjectRequest extends MapPropObj {

	public static final String DUE_TIME = "dueTime";
	public static final String START_TIME = "startTime";

	@ApiModelProperty(required = true)
	public void setName(String name) {
		props.put(NAME, name);
	}

	@NotNull(groups = ValidateOnCreate.class)
	@Size(min = 1, max = RestConstants.VALID_NAME_SIZE)
	public String getName() {
		return getProp(NAME);
	}

	public void setDescription(String description) {
		props.put(DESCRIPTION, description);
	}

	@Size(min = 1, max = RestConstants.VALID_DESCRIPTION_SIZE)
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

	@Future(groups = ValidateOnCreate.class)
	public Date getDueTime() {
		return getProp(DUE_TIME);
	}

	public boolean isRemovingName() {
		return (getName() == null && getPropMap().containsKey(
				ProjectRequest.NAME));
	}

}
