package com.workstream.rest.model;

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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
