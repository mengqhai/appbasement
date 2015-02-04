package com.workstream.rest.model;

import javax.validation.Valid;

import org.activiti.workflow.simple.definition.WorkflowDefinition;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel
public class ModelWorkflowDefRequest {

	@ApiModelProperty(required = true)
	private WorkflowDefinition workflow;

	private String comment;

	@Valid
	// cascade validation
	public WorkflowDefinition getWorkflow() {
		return workflow;
	}

	public void setWorkflow(WorkflowDefinition workflow) {
		this.workflow = workflow;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
