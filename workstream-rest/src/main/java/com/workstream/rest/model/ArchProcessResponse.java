package com.workstream.rest.model;

import java.util.Date;

import org.activiti.engine.history.HistoricProcessInstance;

public class ArchProcessResponse extends
		InnerWrapperObj<HistoricProcessInstance> {

	private HistoricProcessInstance process;

	public ArchProcessResponse(HistoricProcessInstance process) {
		super(process);
		this.process = process;
	}

	public String getId() {
		return process.getId();
	}

	public String getProcessDefinitionId() {
		return process.getProcessDefinitionId();
	}

	public Date getStartTime() {
		return process.getStartTime();
	}

	public Date getEndTime() {
		return process.getEndTime();
	}

	public Long getDurationInMillis() {
		return process.getDurationInMillis();
	}

	public String getStartUserId() {
		return process.getStartUserId();
	}

	public String getDeleteReason() {
		return process.getDeleteReason();
	}

	public String getOrgId() {
		return process.getTenantId();
	}

	public String getName() {
		return process.getName();
	}
	
	

}
