package com.workstream.rest.controller;

import java.util.List;

import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.workstream.core.exception.ResourceNotFoundException;
import com.workstream.core.model.Project;
import com.workstream.core.service.CoreFacadeService;
import com.workstream.rest.model.ProjectRequest;
import com.workstream.rest.model.ProjectResponse;
import com.workstream.rest.model.TaskRequest;
import com.workstream.rest.model.TaskResponse;

@Api(value = "projects", description = "Project related operations")
@RestController
@RequestMapping(value = "/projects", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProjectController {

	private final static Logger log = LoggerFactory
			.getLogger(ProjectController.class);

	@Autowired
	private CoreFacadeService core;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ProjectResponse getProject(@PathVariable("id") Long projectId) {
		Project proj = core.getProjectService().getProject(projectId);
		if (proj == null) {
			throw new ResourceNotFoundException("No such project");
		}
		return new ProjectResponse(proj);
	}

	@ApiOperation(value = "Partially update a project")
	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	public void updateProject(@PathVariable("id") Long projectId,
			@RequestBody ProjectRequest projectReq) {
		core.getProjectService().updateProject(projectId,
				projectReq.getPropMap());
	}

	@ApiOperation(value = "Delete a project", notes = "A cascade delete action")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteProject(@PathVariable("id") Long projectId)
			throws ResourceNotFoundException {
		core.deleteProject(projectId);
		log.debug("Project deleted {}", projectId);
	}

	@ApiOperation(value = "Create a task in the project")
	@RequestMapping(value = "/{id}/tasks", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public TaskResponse createTaskInProject(@PathVariable("id") Long projectId,
			@ApiParam(required = true) @RequestBody TaskRequest taskReq) {
		Task task = core.createTaskInProject(projectId, taskReq.getName(),
				taskReq.getDescription(), taskReq.getDueDate(),
				taskReq.getAssignee(), taskReq.getPriority());
		return new TaskResponse(task);
	}

	@RequestMapping(value = "/{id}/tasks", method = RequestMethod.GET)
	public List<TaskResponse> getTasksInProject(
			@PathVariable("id") Long projectId)
			throws ResourceNotFoundException {
		List<Task> tasks = core.getProjectService().filterTask(projectId);
		List<TaskResponse> respList = TaskResponse.toRespondList(tasks);
		return respList;
	}

}
