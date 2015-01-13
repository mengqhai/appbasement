package com.workstream.core.worflow.simple.form;

import org.activiti.workflow.simple.definition.form.ListPropertyDefinition;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("list")
public class WsListPropertyDefinition extends ListPropertyDefinition {

	public WsListPropertyDefinition() {
		super();
		setType("list");
	}

}
