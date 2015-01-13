package com.workstream.core.worflow.simple.form;

import org.activiti.workflow.simple.definition.form.NumberPropertyDefinition;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("number")
public class WsNumberPropertyDefinition extends NumberPropertyDefinition {

	public WsNumberPropertyDefinition() {
		super();
		setType("number");
	}

}
