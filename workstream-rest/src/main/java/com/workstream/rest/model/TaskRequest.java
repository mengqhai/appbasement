package com.workstream.rest.model;

import java.util.Date;

import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wordnik.swagger.annotations.ApiModelProperty;
import com.workstream.rest.RestConstants;
import com.workstream.rest.validation.NotRemovable;
import com.workstream.rest.validation.ValidateOnCreate;
import com.workstream.rest.validation.ValidateOnUpdate;

@NotRemovable(groups = ValidateOnUpdate.class, value = { TaskRequest.NAME })
public class TaskRequest extends MapPropObj {

	public static final String ASSIGNEE = "assignee";
	private static final String DUE_DATE = "dueDate";

	@ApiModelProperty(required = true)
	public void setName(String name) {
		props.put(NAME, name);
	}

	@NotNull(groups = ValidateOnCreate.class)
	@Size(max = RestConstants.VALID_NAME_SIZE, min = 1)
	public String getName() {
		return getProp(NAME);
	}

	public void setDescription(String description) {
		props.put(DESCRIPTION, description);
	}

	@Size(max = RestConstants.VALID_DESCRIPTION_SIZE, min = 1)
	public String getDescription() {
		return getProp(DESCRIPTION);
	}

	public void setDueDate(Date dueTime) {
		props.put(DUE_DATE, dueTime);
	}

	@Future(groups = ValidateOnCreate.class)
	public Date getDueDate() {
		return getProp(DUE_DATE);
	}

	public void setAssignee(String assignee) {
		props.put(ASSIGNEE, assignee);
	}

	@Size(min = 1, max = RestConstants.VALID_NAME_SIZE)
	public String getAssignee() {
		return getProp(ASSIGNEE);
	}

	public void setPriority(Integer priority) {
		props.put("priority", priority);
	}

	@Max(100)
	@Min(1)
	public Integer getPriority() {
		return getProp("priority");
	}

	@JsonIgnore
	public void setOrgId(Long orgId) {
		// do nothing
	}

	@JsonIgnore
	public void setProjectId(Long projectId) {
		// do nothing
	}

}
