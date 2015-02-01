package com.workstream.rest.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.workstream.rest.RestConstants;

public class OrgRequest extends MapPropObj {

	public static final String IDENTIFIER = "identifier";

	public void setName(String name) {
		props.put(NAME, name);
	}

	@NotNull
	@Size(max = RestConstants.VALID_NAME_SIZE, min = 1)
	public String getName() {
		return getProp(NAME);
	}

	public void setIdentifier(String identifier) {
		props.put(IDENTIFIER, identifier);
	}

	@Size(max = RestConstants.VALID_ORG_IDENTIFIER, min = 1)
	public String getIdentifier() {
		return getProp(IDENTIFIER);
	}

	public void setDescription(String description) {
		props.put(DESCRIPTION, description);
	}

	@Size(max = RestConstants.VALID_DESCRIPTION_SIZE, min = 1)
	public String getDescription() {
		return getProp(DESCRIPTION);
	}

}
