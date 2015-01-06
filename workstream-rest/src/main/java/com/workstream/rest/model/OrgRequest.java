package com.workstream.rest.model;

public class OrgRequest extends MapPropObj {

	public static final String IDENTIFIER = "identifier";
	public void setName(String name) {
		props.put(NAME, name);
	}

	public String getName() {
		return getProp(NAME);
	}

	public void setIdentifier(String identifier) {
		props.put(IDENTIFIER, identifier);
	}

	public String getIdentifier() {
		return getProp(IDENTIFIER);
	}

	public void setDescription(String description) {
		props.put(DESCRIPTION, description);
	}

	public String getDescription() {
		return getProp(DESCRIPTION);
	}

}
