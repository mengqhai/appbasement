package com.workstream.core.model;

import java.util.Date;

public class RevisionEntry {

	private String userId;

	private String comment;

	private Date time;

	public RevisionEntry() {
		super();
	}

	public RevisionEntry(String userId, String comment, Date time) {
		super();
		this.userId = userId;
		this.comment = comment;
		this.time = time;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String description) {
		this.comment = description;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
}
