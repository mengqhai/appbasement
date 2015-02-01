package com.workstream.rest.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel
public class LoginRequest {

	@ApiModelProperty(required = true, position = 1)
	private String userId;

	@ApiModelProperty(required = true, position = 2)
	private String password;

	public LoginRequest() {
	}

	@NotNull
	@Size(min = 1)
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@NotNull
	@Size(min = 1)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
