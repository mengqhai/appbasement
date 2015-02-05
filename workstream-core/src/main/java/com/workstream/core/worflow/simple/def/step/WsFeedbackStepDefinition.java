package com.workstream.core.worflow.simple.def.step;

import org.activiti.workflow.simple.definition.FeedbackStepDefinition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("feedback-step")
public class WsFeedbackStepDefinition extends FeedbackStepDefinition {
	
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
