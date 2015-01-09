package com.workstream.rest.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.activiti.engine.task.Task;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class TaskResponse {

	private Task task;

	public TaskResponse(Task task) {
		this.task = task;
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

	public static List<TaskResponse> toRespondList(List<Task> tasks) {
		List<TaskResponse> respList = new ArrayList<TaskResponse>();
		for (Task task : tasks) {
			respList.add(new TaskResponse(task));
		}
		return respList;
	}

}
