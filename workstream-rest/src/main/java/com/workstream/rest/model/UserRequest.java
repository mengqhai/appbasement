package com.workstream.rest.model;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class UserRequest extends MapPropObj {

	public static final String PASSWORD = "password";

	private static final String EMAIL = "email";

	public static final String LAST_NAME = "lastName";

	public static final String FIRST_NAME = "firstName";

	public static final String ID = "id";

	@ApiModelProperty(required = true)
	public void setId(String userId) {
		props.put(ID, userId);
	}

	public String getId() {
		return getProp(ID);
	}

	public String getFirstName() {
		return getProp(FIRST_NAME);
	}

	public void setFirstName(String firstName) {
		props.put(FIRST_NAME, firstName);
	}

	public String getLastName() {
		return getProp(LAST_NAME);
	}

	public void setLastName(String lastName) {
		props.put(LAST_NAME, lastName);
	}

	public void setEmail(String email) {
		props.put(EMAIL, email);
	}

	public String getEmail() {
		return getProp(EMAIL);
	}

	public void setPassword(String password) {
		props.put(PASSWORD, password);
	}

	public String getPassword() {
		return getProp(PASSWORD);
	}

}
