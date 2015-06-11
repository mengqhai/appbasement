package com.workstream.core.worflow.simple.def;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.activiti.workflow.simple.definition.StepDefinition;
import org.activiti.workflow.simple.definition.WorkflowDefinition;
import org.activiti.workflow.simple.definition.form.FormDefinition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class WsWorkflowDefinition extends WorkflowDefinition {

	@Override
	public String getName() {
		return super.getName();
	}

	@Override
	/**
	 * must not ignore in json we need the model_[model_id] string as Id to be saved in json
	 */
	public String getId() {
		return super.getId();
	}

	@Override
	/**
	 * must not ignore in json we need the model_[model_id] string as Id to be saved in json
	 */
	public void setId(String id) {
		super.setId(id);
	}

	@JsonIgnore
	@Override
	public String getKey() {
		return super.getKey();
	}

	@JsonIgnore
	@Override
	public void setKey(String key) {
		super.setKey(key);
	}

	@JsonIgnore
	@Override
	public void setCategory(String category) {
		super.setCategory(category);
	}

	@Override
	public String getDescription() {
		return super.getDescription();
	}

	@JsonIgnore
	@Override
	public String getCategory() {
		return super.getCategory();
	}

	@JsonIgnore
	@Override
	public Map<String, Object> getParameters() {
		return super.getParameters();
	}

	// @JsonIgnore
	@Override
	public void setName(String name) {
		super.setName(name);
	}

	@JsonIgnore
	@Override
	public void setParameters(Map<String, Object> parameters) {
		super.setParameters(parameters);
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

	@Valid
	@Override
	public List<StepDefinition> getSteps() {
		return super.getSteps();
	}

}
