package com.workstream.rest.model;

import org.activiti.engine.task.Attachment;

public class AttachmentResponse extends InnerWrapperObj<Attachment> {

	private Attachment attachment;

	public AttachmentResponse(Attachment attachment) {
		super(attachment);
		this.attachment = attachment;
	}

	public String getId() {
		return attachment.getId();
	}

	public String getName() {
		return attachment.getName();
	}

	public String getDescription() {
		return attachment.getDescription();
	}

	public String getType() {
		return attachment.getType();
	}

	public String getTaskId() {
		return attachment.getTaskId();
	}

	public String getProcessInstanceId() {
		return attachment.getProcessInstanceId();
	}

	public String getUrl() {
		return attachment.getUrl();
	}

	public String getUserId() {
		return attachment.getUserId();
	}

}
