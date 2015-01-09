package com.workstream.rest.controller;

import java.util.List;
import java.util.Map;

import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.workstream.core.exception.ResourceNotFoundException;
import com.workstream.core.service.CoreFacadeService;
import com.workstream.rest.model.TaskResponse;

@Api(value = "tasks", description = "Task related operations")
@RestController
@RequestMapping(value = "/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskController {

	@Autowired
	private CoreFacadeService core;

	@ApiOperation(value = "Query the tasks assigned to the current user")
	@RequestMapping(value = "/_my", method = RequestMethod.GET)
	public List<TaskResponse> getMyAssigneeTasks() {
		String userId = core.getAuthUserId();
		List<Task> tasks = core.getProjectService()
				.filterTaskByAssignee(userId);
		List<TaskResponse> respList = TaskResponse.toRespondList(tasks);
		return respList;
	}

	@ApiOperation(value = "Get the task for id")
	@RequestMapping(value = "/{id:\\d+}", method = RequestMethod.GET)
	public TaskResponse getTask(@PathVariable("id") String taskId) {
		Task task = core.getProjectService().getTask(taskId);
		if (task == null) {
			throw new ResourceNotFoundException("No such task, archived?");
		}
		TaskResponse resp = new TaskResponse(core.getProjectService().getTask(
				taskId));
		return resp;
	}

	@ApiOperation(value = "Complete the task")
	@RequestMapping(value = "/{id:\\d+}/_complete", method = RequestMethod.PUT)
	public void completeTask(@PathVariable("id") String taskId,
			@RequestBody Map<String, Object> vars) {
		core.getProcessService().completeTask(taskId, vars);
	}

}
