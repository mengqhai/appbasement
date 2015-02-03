package com.workstream.rest.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mangofactory.swagger.annotations.ApiIgnore;
import com.workstream.rest.RestConstants;
import com.workstream.rest.validation.NotRemovable;
import com.workstream.rest.validation.ValidateOnCreate;
import com.workstream.rest.validation.ValidateOnUpdate;

@NotRemovable(value = { MapPropObj.NAME }, groups = ValidateOnUpdate.class)
public class GroupRequest extends MapPropObj {

	private boolean groupPropSet;
	private boolean groupXPropSet;

	public void setName(@NotNull String name) {
		props.put(NAME, name);
		groupPropSet = true;
	}

	@NotNull(groups = ValidateOnCreate.class)
	@Size(min = 1, max = RestConstants.VALID_NAME_SIZE)
	public String getName() {
		return getProp(NAME);
	}

	public void setDescription(String description) {
		props.put(DESCRIPTION, description);
		groupXPropSet = true;
	}

	@Size(min = 1, max = RestConstants.VALID_DESCRIPTION_SIZE)
	public String getDescription() {
		return getProp(DESCRIPTION);
	}

	@ApiIgnore
	@JsonIgnore
	public boolean isGroupPropSet() {
		return groupPropSet;
	}

	@ApiIgnore
	@JsonIgnore
	public boolean isGroupXPropSet() {
		return groupXPropSet;
	}

	public boolean isRemovingName() {
		return (getName() == null && getPropMap().containsKey(
				ProjectRequest.NAME));
	}

}
