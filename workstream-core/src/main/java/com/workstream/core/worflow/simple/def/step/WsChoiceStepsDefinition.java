package com.workstream.core.worflow.simple.def.step;

import org.activiti.workflow.simple.definition.ChoiceStepsDefinition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("choice-step")
public class WsChoiceStepsDefinition extends ChoiceStepsDefinition {
	
	@JsonIgnore
	@Override
	public String getId() {
		return super.getId();
	}

	@JsonIgnore
	@Override
	public void setId(String id) {
		super.setId(id);
	}


}
