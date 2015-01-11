package com.workstream.rest.model;

import java.util.Date;

import org.activiti.engine.task.Task;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class TaskResponse extends InnerWrapperObj<Task> {

	private Task task;

	public TaskResponse(Task task) {
		super(task);
		this.task = inner;
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

	public String getCreator() {
		return task.getOwner();
	}

	public String getProcessInstanceId() {
		return task.getProcessInstanceId();
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

	public String getParentTaskId() {
		return task.getParentTaskId();
	}

	public String getOrgId() {
		return task.getTenantId();
	}

	public String getProjectId() {
		return task.getCategory();
	}

	public String getAssignee() {
		return task.getAssignee();
	}

}
