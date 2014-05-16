package com.angularjsplay.model;

import com.appbasement.model.User;

public class LoginResult {

	private boolean success;
	
	private String failReason;
	
	private User user;
	
	public LoginResult() {
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getFailReason() {
		return failReason;
	}

	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	

}
