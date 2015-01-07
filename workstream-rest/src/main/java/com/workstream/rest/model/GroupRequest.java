package com.workstream.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mangofactory.swagger.annotations.ApiIgnore;

public class GroupRequest extends MapPropObj {

	private boolean groupPropSet;
	private boolean groupXPropSet;

	public void setName(String name) {
		props.put(NAME, name);
		groupPropSet = true;
	}

	public String getName() {
		return getProp(NAME);
	}

	public void setDescription(String description) {
		props.put(DESCRIPTION, description);
		groupXPropSet = true;
	}

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

}
