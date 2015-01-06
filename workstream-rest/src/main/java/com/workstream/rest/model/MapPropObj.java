package com.workstream.rest.model;

import java.util.HashMap;
import java.util.Map;

public class MapPropObj {

	public static final String DESCRIPTION = "description";
	public static final String NAME = "name";
	protected Map<String, Object> props = new HashMap<String, Object>();

	@SuppressWarnings("unchecked")
	protected <T> T getProp(String key) {
		return (T) props.get(key);
	}

}
