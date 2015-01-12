package com.workstream.rest.model;

import java.util.Date;

import org.activiti.engine.history.HistoricProcessInstance;

public class HiProcessResponse extends InnerWrapperObj<HistoricProcessInstance> {

	private HistoricProcessInstance hiProcess;

	public HiProcessResponse(HistoricProcessInstance hiProcess) {
		super(hiProcess);
		this.hiProcess = hiProcess;
	}

	public String getId() {
		return hiProcess.getId();
	}

	public String getProcessDefinitionId() {
		return hiProcess.getProcessDefinitionId();
	}

	public Date getStartTime() {
		return hiProcess.getStartTime();
	}

	public Date getEndTime() {
		return hiProcess.getEndTime();
	}

	public Long getDurationInMillis() {
		return hiProcess.getDurationInMillis();
	}

	public String getStartUserId() {
		return hiProcess.getStartUserId();
	}

	public String getStartActivityId() {
		return hiProcess.getStartActivityId();
	}

	public String getDeleteReason() {
		return hiProcess.getDeleteReason();
	}

	public String getTenantId() {
		return hiProcess.getTenantId();
	}

	public String getName() {
		return hiProcess.getName();
	}

}
