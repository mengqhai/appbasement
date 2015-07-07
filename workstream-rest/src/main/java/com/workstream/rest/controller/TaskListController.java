package com.workstream.rest.controller;

import java.util.List;

import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;
import com.workstream.core.exception.ResourceNotFoundException;
import com.workstream.core.model.TaskList;
import com.workstream.core.service.ProjectService;
import com.workstream.rest.model.InnerWrapperObj;
import com.workstream.rest.model.TaskListRequest;
import com.workstream.rest.model.TaskListResponse;
import com.workstream.rest.model.TaskResponse;

@Api(value = "tasklists", description = "Task list related operations")
@RestController
@RequestMapping(value = "/tasklists", produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskListController {

	@Autowired
	private ProjectService proSer;

	@RequestMapping(value = "/{id:\\d+}", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForTaskList(#taskListId) && isAuthMemberCapableForTaskListRetrieve(#taskListId)")
	public TaskListResponse getTaskList(@PathVariable("id") Long taskListId) {
		TaskList taskList = proSer.getTaskList(taskListId);
		if (taskList == null) {
			throw new ResourceNotFoundException("No such task list.");
		}
		return new TaskListResponse(taskList);
	}

	@RequestMapping(value = "/{id:\\d+}", method = RequestMethod.PATCH)
	@PreAuthorize("isAuthInOrgForTaskList(#taskListId) && isAuthMemberCapableForTaskListUpdate(#taskListId)")
	public void updateTaskList(@PathVariable("id") Long taskListId,
			@RequestBody(required = true) TaskListRequest patch) {
		proSer.updateTaskList(taskListId, patch.getPropMap());
	}

	@RequestMapping(value = "/{id:\\d+}/tasks/{taskId:\\d+}", method = RequestMethod.PUT)
	@PreAuthorize("isAuthInOrgForTaskList(#taskListId) && isAuthMemberCapableForTaskListUpdate(#taskListId)")
	public void addTaskToList(@PathVariable("id") Long taskListId,
			@PathVariable("taskId") String taskId) {
		proSer.addTaskToList(taskListId, taskId);
	}

	@RequestMapping(value = "/{id:\\d+}/tasks", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForTaskList(#taskListId) && isAuthMemberCapableForTaskListRetrieve(#taskListId)")
	public List<TaskResponse> getTasksInList(@PathVariable("id") Long taskListId) {
		List<Task> tasks = proSer.filterTaskForTaskList(taskListId);
		return InnerWrapperObj.valueOf(tasks, TaskResponse.class);
	}

	@RequestMapping(value = "/{id:\\d+}/tasks/{taskId:\\d+}", method = RequestMethod.DELETE)
	@PreAuthorize("isAuthInOrgForTaskList(#taskListId) && isAuthMemberCapableForTaskListUpdate(#taskListId)")
	public void removeTaskFromList(@PathVariable("id") Long taskListId,
			@PathVariable("taskId") String taskId) {
		proSer.removeTaskFromList(taskListId, taskId);
	}

	public void deleteTaskList(Long taskListId) {

	}

}
