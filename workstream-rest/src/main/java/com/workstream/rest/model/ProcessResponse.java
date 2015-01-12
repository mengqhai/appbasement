package com.workstream.rest.model;

import org.activiti.engine.runtime.ProcessInstance;

public class ProcessResponse extends InnerWrapperObj<ProcessInstance> {

	private ProcessInstance process;

	public ProcessResponse(ProcessInstance process) {
		super(process);
		this.process = inner;
	}

	public String getId() {
		return process.getId();
	}

	public String getProcessDefinitionId() {
		return process.getProcessDefinitionId();
	}

	public boolean isEnded() {
		return process.isEnded();
	}

	public String getProcessDefinitionName() {
		return process.getProcessDefinitionName();
	}

	public Integer getProcessDefinitionVersion() {
		return process.getProcessDefinitionVersion();
	}

	public String getDeploymentId() {
		return process.getDeploymentId();
	}

	public String getOrgId() {
		return process.getTenantId();
	}

	public String getName() {
		return process.getName();
	}

	public String getActivityId() {
		return process.getActivityId();
	}
}
