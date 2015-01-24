package com.workstream.rest.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.workstream.core.model.CoreEvent;
import com.workstream.core.model.CoreEvent.EventType;
import com.workstream.core.model.CoreEvent.TargetType;
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

	@JsonIgnore
	public CoreEvent getEvent() {
		// the event attribute is always eagerly fetched from DB
		return notification.getEvent();
	}

	public TargetType getTargetType() {
		return notification.getEvent().getTargetType();
	}

	public String getTargetId() {
		return notification.getEvent().getTargetId();
	}

	public TargetType getParentType() {
		return notification.getEvent().getParentType();
	}

	public String getParentId() {
		return notification.getEvent().getParentId();
	}

	public String getActionUserId() {
		return notification.getEvent().getUserId();
	}

	public EventType getEventType() {
		return notification.getEvent().getEventType();
	}

	public Long getEventId() {
		return notification.getEvent().getId();
	}

	public String getAdditionalInfo() {
		return notification.getEvent().getAdditionalInfo();
	}

	public InnerWrapperObj<?> getTarget() {
		return target;
	}

	public void setTarget(InnerWrapperObj<?> target) {
		this.target = target;
	}

	public Date getCreatedAt() {
		return notification.getCreatedAt();
	}

}
