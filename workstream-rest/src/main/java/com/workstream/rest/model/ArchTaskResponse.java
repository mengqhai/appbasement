package com.workstream.rest.model;

import java.util.Date;

import org.activiti.engine.history.HistoricTaskInstance;

public class ArchTaskResponse extends InnerWrapperObj<HistoricTaskInstance> {
	private HistoricTaskInstance task;

	public ArchTaskResponse(HistoricTaskInstance task) {
		super(task);
		this.task = task;
	}

	public Date getTime() {
		return task.getTime();
	}

	public String getId() {
		return task.getId();
	}

	public String getName() {
		return task.getName();
	}

	public String getDescription() {
		return task.getDescription();
	}

	public int getPriority() {
		return task.getPriority();
	}

	public String getDeleteReason() {
		return task.getDeleteReason();
	}

	public String getCreator() {
		return task.getOwner();
	}

	public Date getStartTime() {
		return task.getStartTime();
	}

	public String getAssignee() {
		return task.getAssignee();
	}

	public Date getEndTime() {
		return task.getEndTime();
	}

	public String getProcessInstanceId() {
		return task.getProcessInstanceId();
	}

	public Long getDurationInMillis() {
		return task.getDurationInMillis();
	}

	public Long getWorkTimeInMillis() {
		return task.getWorkTimeInMillis();
	}

	public Date getClaimTime() {
		return task.getClaimTime();
	}

	public String getProcessDefinitionId() {
		return task.getProcessDefinitionId();
	}

	public Date getCreateTime() {
		return task.getCreateTime();
	}

	public Date getDueDate() {
		return task.getDueDate();
	}

	public String getProjectId() {
		return task.getCategory();
	}

	public String getParentTaskId() {
		return task.getParentTaskId();
	}

	public String getOrgId() {
		return task.getTenantId();
	}

}
