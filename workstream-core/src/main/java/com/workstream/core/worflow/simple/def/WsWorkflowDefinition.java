package com.workstream.core.worflow.simple.def;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.activiti.workflow.simple.definition.StepDefinition;
import org.activiti.workflow.simple.definition.WorkflowDefinition;
import org.activiti.workflow.simple.definition.form.FormDefinition;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class WsWorkflowDefinition extends WorkflowDefinition {

	@Override
	public String getName() {
		return super.getName();
	}

	@Override
	public String getId() {
		return super.getId();
	}

	@Override
	public String getKey() {
		return super.getKey();
	}

	@Override
	public String getDescription() {
		return super.getDescription();
	}

	@Override
	public String getCategory() {
		return super.getCategory();
	}

	@Override
	public Map<String, Object> getParameters() {
		return super.getParameters();
	}

	@Valid
	@Override
	public FormDefinition getStartFormDefinition() {
		return super.getStartFormDefinition();
	}

	@JsonDeserialize(as = WsFormDefinition.class)
	@Override
	public void setStartFormDefinition(FormDefinition startFormDefinition) {
		super.setStartFormDefinition(startFormDefinition);
	}

	@Override
	public List<StepDefinition> getSteps() {
		return super.getSteps();
	}

}
