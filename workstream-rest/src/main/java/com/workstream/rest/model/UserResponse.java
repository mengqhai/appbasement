package com.workstream.rest.model;

import java.util.Date;

import org.activiti.engine.identity.User;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.workstream.core.model.UserX;

@JsonInclude(Include.NON_NULL)
public class UserResponse extends InnerWrapperObj<User> {

	private User user;
	private UserX userX;

	public UserResponse(User user) {
		super(user);
		this.user = inner;
	}

	public UserResponse(User user, UserX userX) {
		this(user);
		this.userX = userX;
		this.user = inner;
	}

	public String getId() {
		return user.getId();
	}

	public String getFirstName() {
		return user.getFirstName();
	}

	public String getLastName() {
		return user.getLastName();
	}

	public String getEmail() {
		return user.getEmail();
	}

	public boolean isPictureSet() {
		return user.isPictureSet();
	}

	public Date getCreatedAt() {
		if (userX == null) {
			return null;
		}
		return userX.getCreatedAt();
	}

}
