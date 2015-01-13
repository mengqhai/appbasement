package com.workstream.core.worflow.simple.form;

import org.activiti.workflow.simple.definition.form.BooleanPropertyDefinition;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("boolean")
public class WsBooleanPropertyDefinition extends BooleanPropertyDefinition {

	public WsBooleanPropertyDefinition() {
		super();
		setType("boolean");
	}

}
