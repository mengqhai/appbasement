package com.workstream.rest.model;

import java.util.Date;

import org.activiti.engine.task.Comment;

public class CommentResponse extends InnerWrapperObj<Comment> {

	private Comment comment;

	public CommentResponse(Comment comment) {
		super(comment);
		this.comment = comment;
	}

	public String getId() {
		return comment.getId();
	}

	public String getUserId() {
		return comment.getUserId();
	}

	public Date getTime() {
		return comment.getTime();
	}

	public String getTaskId() {
		return comment.getTaskId();
	}

	public String getProcessInstanceId() {
		return comment.getProcessInstanceId();
	}

	public String getType() {
		return comment.getType();
	}

	public String getFullMessage() {
		return comment.getFullMessage();
	}

}
