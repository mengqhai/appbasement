package com.workstream.rest.model;

import java.util.Date;

import org.activiti.engine.task.Event;

public class EventResponse extends InnerWrapperObj<Event> {

	private Event event;

	public EventResponse(Event event) {
		super(event);
		this.event = event;
	}

	public String getId() {
		return event.getId();
	}

	public String getAction() {
		return event.getAction();
	}

	public String getMessage() {
		return event.getMessage();
	}

	public String getUserId() {
		return event.getUserId();
	}

	public Date getTime() {
		return event.getTime();
	}

	public String getTaskId() {
		return event.getTaskId();
	}

	public String getProcessInstanceId() {
		return event.getProcessInstanceId();
	}
	
	

}
