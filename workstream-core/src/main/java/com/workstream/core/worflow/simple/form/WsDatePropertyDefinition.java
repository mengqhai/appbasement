package com.workstream.core.worflow.simple.form;

import org.activiti.workflow.simple.definition.form.DatePropertyDefinition;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("date")
public class WsDatePropertyDefinition extends DatePropertyDefinition {

	public WsDatePropertyDefinition() {
		super();
		setType("date");
	}

	public WsDatePropertyDefinition(boolean showTime) {
		super(showTime);
		setType("date");
	}

}
