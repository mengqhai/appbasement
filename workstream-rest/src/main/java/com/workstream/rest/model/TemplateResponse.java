package com.workstream.rest.model;

import org.activiti.engine.repository.ProcessDefinition;

public class TemplateResponse extends InnerWrapperObj<ProcessDefinition> {

	private ProcessDefinition template;

	public TemplateResponse(ProcessDefinition template) {
		super(template);
		this.template = inner;
	}

	public String getId() {
		return template.getId();
	}

	public String getName() {
		return template.getName();
	}

	public String getDescription() {
		return template.getDescription();
	}

	public int getVersion() {
		return template.getVersion();
	}

	public String getDeploymentId() {
		return template.getDeploymentId();
	}

	public String getOrgId() {
		return template.getTenantId();
	}

}
