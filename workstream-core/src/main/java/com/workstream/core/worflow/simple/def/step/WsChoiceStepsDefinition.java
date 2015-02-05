package com.workstream.core.worflow.simple.def.step;

import java.util.List;

import org.activiti.workflow.simple.definition.ChoiceStepsDefinition;
import org.activiti.workflow.simple.definition.ListConditionStepDefinition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

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

	@JsonSerialize(contentAs = WsListConditionStepDefinition.class)
	@Override
	public List<ListConditionStepDefinition<ChoiceStepsDefinition>> getStepList() {
		return super.getStepList();
	}

}
