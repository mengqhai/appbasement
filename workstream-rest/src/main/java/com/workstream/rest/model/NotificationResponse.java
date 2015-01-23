package com.workstream.rest.model;

import com.workstream.core.model.CoreEvent;
import com.workstream.core.model.Notification;

public class NotificationResponse extends InnerWrapperObj<Notification> {

	private Notification notification;

	private InnerWrapperObj<?> target;

	public NotificationResponse(Notification notification) {
		super(notification);
		this.notification = notification;
	}

	public NotificationResponse(Notification notification,
			InnerWrapperObj<?> target) {
		this(notification);
		this.target = target;
	}

	public String getUserId() {
		return notification.getUserId();
	}

	public String getMessage() {
		return notification.getMessage();
	}

	public Long getId() {
		return notification.getId();
	}

	public boolean isRead() {
		return notification.isRead();
	}

	public Long getOrgId() {
		return notification.getOrgId();
	}

	public CoreEvent getEvent() {
		// the event attribute is always eagerly fetched from DB
		return notification.getEvent();
	}

	public InnerWrapperObj<?> getTarget() {
		return target;
	}

	public void setTarget(InnerWrapperObj<?> target) {
		this.target = target;
	}
	
	

}
