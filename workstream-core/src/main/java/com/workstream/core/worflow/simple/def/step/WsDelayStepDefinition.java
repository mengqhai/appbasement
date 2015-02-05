package com.workstream.core.worflow.simple.def.step;

import org.activiti.workflow.simple.definition.DelayStepDefinition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("delay-step")
public class WsDelayStepDefinition extends DelayStepDefinition {
	
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
