package com.workstream.rest.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.identity.Group;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.workstream.core.exception.ResourceNotFoundException;
import com.workstream.core.service.CoreFacadeService;
import com.workstream.rest.model.InnerWrapperObj;
import com.workstream.rest.model.TaskRequest;
import com.workstream.rest.model.TaskResponse;

@Api(value = "tasks", description = "Task related operations")
@RestController
@RequestMapping(value = "/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskController {
	private final static Logger log = LoggerFactory
			.getLogger(TaskController.class);

	@Autowired
	private CoreFacadeService core;

	@ApiOperation(value = "Query the tasks assigned to the current user")
	@RequestMapping(value = "/_my", method = RequestMethod.GET)
	public List<TaskResponse> getMyAssigneeTasks() {
		String userId = core.getAuthUserId();
		List<Task> tasks = core.getProjectService()
				.filterTaskByAssignee(userId);
		List<TaskResponse> respList = InnerWrapperObj.valueOf(tasks,
				TaskResponse.class);
		return respList;
	}

	@ApiOperation(value = "Query the tasks created current user")
	@RequestMapping(value = "/_createdByMe", method = RequestMethod.GET)
	public List<TaskResponse> getMyCreatorTasks() {
		String userId = core.getAuthUserId();
		List<Task> tasks = core.getProjectService().filterTaskByCreator(userId);
		List<TaskResponse> respList = InnerWrapperObj.valueOf(tasks,
				TaskResponse.class);
		return respList;
	}

	@ApiOperation(value = "Query the candidate tasks of the current user")
	@RequestMapping(value = "/_myCandidate", method = RequestMethod.GET)
	public Map<String, List<TaskResponse>> getMyCandidateTask() {
		String userId = core.getAuthUserId();
		List<Group> groups = core.getUserService().filterGroupByUser(userId);
		Map<String, List<TaskResponse>> respMap = new HashMap<String, List<TaskResponse>>(
				groups.size());
		for (Group g : groups) {
			List<Task> tasks = core.getProcessService()
					.filterTaskByCandidateGroup(g.getId());
			List<TaskResponse> respList = InnerWrapperObj.valueOf(tasks,
					TaskResponse.class);
			respMap.put(g.getId(), respList);
		}
		return respMap;
	}

	@ApiOperation(value = "Get the task for id")
	@RequestMapping(value = "/{id:\\d+}", method = RequestMethod.GET)
	public TaskResponse getTask(@PathVariable("id") String taskId) {
		Task task = core.getProjectService().getTask(taskId);
		if (task == null) {
			throw new ResourceNotFoundException("No such task, archived?");
		}
		TaskResponse resp = InnerWrapperObj.valueOf(task, TaskResponse.class);
		return resp;
	}

	@ApiOperation(value = "Complete the task")
	@RequestMapping(value = "/{id:\\d+}/_complete", method = RequestMethod.PUT)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void completeTask(@PathVariable("id") String taskId,
			@RequestBody Map<String, Object> vars) {
		core.getProcessService().completeTask(taskId, vars);
	}

	@ApiOperation(value = "Partially update the task for id")
	@RequestMapping(value = "/{id:\\d+}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateTask(@PathVariable("id") String taskId,
			@ApiParam(required = true) @RequestBody TaskRequest taskReq) {
		core.updateTask(taskId, taskReq.getPropMap());
		log.debug("Updated task {}", taskId);
	}

	@ApiOperation(value = "Delete(cancel) the task for id")
	@RequestMapping(value = "/{id:\\d+}", method = RequestMethod.DELETE)
	public void deleteTask(@PathVariable("id") String taskId) {
		// TODO if it's a task of a running process, trying to delete it will
		// cause activiti exception, so an exception mapping is required
		core.getProjectService().deleteTask(taskId);
		log.debug("Deleted task {}", taskId);
	}
}