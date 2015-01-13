package com.workstream.core.worflow.simple.form;

import org.activiti.workflow.simple.definition.form.TextPropertyDefinition;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Replaces the Activiti TextPropertyDefinition as the type property is null.
 * 
 * @author qinghai
 * 
 */
@JsonTypeName("text")
public class WsTextPropertyDefinition extends TextPropertyDefinition {

	public WsTextPropertyDefinition() {
		super();
		this.setType("text");
	}

}
