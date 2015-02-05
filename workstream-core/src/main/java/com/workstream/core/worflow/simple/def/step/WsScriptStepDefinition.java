package com.workstream.core.worflow.simple.def.step;

import org.activiti.workflow.simple.definition.ScriptStepDefinition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("script-step")
public class WsScriptStepDefinition extends ScriptStepDefinition {
	
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
