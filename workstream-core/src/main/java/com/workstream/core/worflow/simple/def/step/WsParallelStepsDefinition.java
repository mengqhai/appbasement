package com.workstream.core.worflow.simple.def.step;

import org.activiti.workflow.simple.definition.ParallelStepsDefinition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("parallel-step")
public class WsParallelStepsDefinition extends ParallelStepsDefinition {
	
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
