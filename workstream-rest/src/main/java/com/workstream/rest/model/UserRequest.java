package com.workstream.rest.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.wordnik.swagger.annotations.ApiModelProperty;
import com.workstream.core.exception.BadArgumentException;
import com.workstream.rest.RestConstants;
import com.workstream.rest.validation.ValidateOnCreate;

public class UserRequest extends MapPropObj {

	public static final String PASSWORD = "password";

	private static final String EMAIL = "email";

	public static final String LAST_NAME = "lastName";

	public static final String FIRST_NAME = "firstName";

	public static final String ID = "id";

	@ApiModelProperty(required = true)
	public void setId(String userId) {
		if (userId == null || userId.isEmpty()) {
			throw new BadArgumentException();
		}
		props.put(ID, userId);
	}

	@NotNull(groups = ValidateOnCreate.class)
	@Size(min = 1, max = RestConstants.VALID_NAME_SIZE)
	public String getId() {
		return getProp(ID);
	}

	@NotNull(groups = ValidateOnCreate.class)
	@Size(min = 1, max = RestConstants.VALID_NAME_SIZE)
	public String getFirstName() {
		return getProp(FIRST_NAME);
	}

	public void setFirstName(String firstName) {
		if (firstName == null || firstName.isEmpty()) {
			throw new BadArgumentException();
		}
		props.put(FIRST_NAME, firstName);
	}

	@Size(min = 1, max = RestConstants.VALID_NAME_SIZE)
	public String getLastName() {
		return getProp(LAST_NAME);
	}

	public void setLastName(String lastName) {
		props.put(LAST_NAME, lastName);
	}

	public void setEmail(String email) {
		if (email == null || email.isEmpty()) {
			throw new BadArgumentException();
		}
		props.put(EMAIL, email);
	}

	@NotNull(groups = ValidateOnCreate.class)
	public String getEmail() {
		return getProp(EMAIL);
	}

	public void setPassword(String password) {
		if (password == null || password.isEmpty()) {
			throw new BadArgumentException();
		}
		props.put(PASSWORD, password);
	}

	@NotNull(groups = ValidateOnCreate.class)
	@Size(min = RestConstants.VALID_PASSWORD_MIN_SIZE, max = RestConstants.VALID_PASSWORD_MAX_SIZE)
	public String getPassword() {
		return getProp(PASSWORD);
	}

}
