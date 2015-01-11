package com.workstream.rest.model;

public class ModelRequest extends MapPropObj {

	public void setName(String name) {
		props.put(NAME, name);
	}

	public String getName() {
		return getProp(NAME);
	}

}
