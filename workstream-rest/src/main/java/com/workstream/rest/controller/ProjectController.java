package com.workstream.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;
import com.workstream.core.model.Project;
import com.workstream.core.service.CoreFacadeService;
import com.workstream.rest.model.ProjectResponse;

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
		return new ProjectResponse(proj);
	}

}
