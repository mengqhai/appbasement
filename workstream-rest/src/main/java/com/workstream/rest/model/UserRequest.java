package com.workstream.rest.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import com.wordnik.swagger.annotations.ApiModelProperty;
import com.workstream.rest.RestConstants;
import com.workstream.rest.validation.NotRemovable;
import com.workstream.rest.validation.ValidateOnCreate;

@NotRemovable(value = { UserRequest.PASSWORD, UserRequest.EMAIL,
		UserRequest.LAST_NAME, UserRequest.ID })
public class UserRequest extends MapPropObj {

	public static final String PASSWORD = "password";

	public static final String EMAIL = "email";

	public static final String LAST_NAME = "lastName";

	public static final String FIRST_NAME = "firstName";

	public static final String ID = "id";

	@ApiModelProperty(required = true)
	public void setId(String userId) {
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
		props.put(EMAIL, email);
	}

	@NotNull(groups = ValidateOnCreate.class)
	@Email
	public String getEmail() {
		return getProp(EMAIL);
	}

	public void setPassword(String password) {
		props.put(PASSWORD, password);
	}

	@NotNull(groups = ValidateOnCreate.class)
	@Size(min = RestConstants.VALID_PASSWORD_MIN_SIZE, max = RestConstants.VALID_PASSWORD_MAX_SIZE)
	public String getPassword() {
		return getProp(PASSWORD);
	}

}
