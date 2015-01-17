package com.workstream.rest.controller;

import java.util.List;

import org.activiti.engine.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.workstream.core.exception.ResourceNotFoundException;
import com.workstream.core.model.Project;
import com.workstream.core.service.CoreFacadeService;
import com.workstream.rest.model.ArchTaskResponse;
import com.workstream.rest.model.InnerWrapperObj;

@Api(value = "archives")
@RestController
@RequestMapping(value = "/archives", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArchiveController {

	@Autowired
	private CoreFacadeService core;

	@ApiOperation(value = "Retrieves the archived task for a given id")
	@RequestMapping(value = "/tasks/{id}", method = RequestMethod.GET)
	public ArchTaskResponse getArchivedTask(@PathVariable("id") String taskId) {
		HistoricTaskInstance hiTask = core.getProjectService().getArchTask(
				taskId);
		if (hiTask == null) {
			throw new ResourceNotFoundException("No such archived task");
		}

		ArchTaskResponse resp = new ArchTaskResponse(hiTask);
		return resp;
	}

	@ApiOperation(value = "Retrieves the archived task for a given id")
	@RequestMapping(value = "/projects/{projectId}/tasks", method = RequestMethod.GET)
	public List<ArchTaskResponse> getArchTasksInProject(
			@PathVariable("projectId") long projectId) {
		Project project = core.getProjectService().getProject(projectId);
		if (project == null) {
			throw new ResourceNotFoundException("No such project");
		}

		List<HistoricTaskInstance> hiTasks = core.getProjectService()
				.filterArchTask(project);
		return InnerWrapperObj.valueOf(hiTasks, ArchTaskResponse.class);
	}

}
