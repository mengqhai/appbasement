package com.angularjsplay.mvc.rest;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.angularjsplay.exception.ScrumValidationException;
import com.angularjsplay.model.Backlog;
import com.angularjsplay.model.Project;
import com.angularjsplay.model.Sprint;
import com.angularjsplay.mvc.validation.ValidateOnCreate;
import com.angularjsplay.mvc.validation.ValidateOnUpdate;
import com.angularjsplay.service.IScrumService;
import com.appbasement.component.IObjectPatcher;
import com.appbasement.component.PatchedValue;
import com.wordnik.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(value = "/projects", headers = "Accept=application/json", produces = "application/json")
public class ProjectController {

	@Autowired
	IObjectPatcher objectPatcher;

	@Autowired
	IScrumService scrumService;

	public ProjectController() {
	}

	@ApiOperation(value = "list projects", notes = "list all the projects")
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<Project> listProjects() {
		return scrumService.getAll(Project.class);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Project getProject(@PathVariable("id") long id) {
		return scrumService.getById(Project.class, id);
	}

	@RequestMapping(value = "/{id}", method = { RequestMethod.PUT,
			RequestMethod.PATCH }, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void updateProject(@PathVariable("id") long id,
			@RequestBody @Validated(ValidateOnUpdate.class) Project patch,
			BindingResult bResult) {
		if (bResult.hasErrors()) {
			throw new ScrumValidationException(bResult);
		}

		Project existing = scrumService.getById(Project.class, id);
		Map<Field, PatchedValue> patchedResult = objectPatcher.patchObject(
				existing, patch);
		if (!patchedResult.isEmpty()) {
			scrumService.save(existing);
		}
	}

	@RequestMapping(method = RequestMethod.POST, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.CREATED)
	@ResponseBody
	public Project createProject(
			@RequestBody @Validated(ValidateOnCreate.class) Project project,
			BindingResult bResult) {
		if (bResult.hasErrors()) {
			throw new ScrumValidationException(bResult);
		}
		scrumService.save(project);
		return project;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteProject(@PathVariable("id") long id) {
		scrumService.deleteById(Project.class, id);
	}

	@RequestMapping(value = "/{id}/backlogs", method = RequestMethod.GET)
	@ResponseBody
	public Collection<Backlog> listBacklogsForProject(
			@PathVariable("id") long id) {
		return scrumService.getAllBacklogsForProject(id);
	}

	@RequestMapping(value = "/{id}/backlogs", method = RequestMethod.GET, params = {
			"first", "max" })
	@ResponseBody
	public Collection<Backlog> listBacklogsForProject(
			@PathVariable("id") long id, @RequestParam("first") int first,
			@RequestParam("max") int max) {
		return scrumService.getBacklogsForProject(id, first, max);
	}

	@RequestMapping(value = "/{id}/backlogs", method = RequestMethod.GET, params = { "count" })
	@ResponseBody
	public Long getBacklogCountForProject(@PathVariable("id") long id) {
		return scrumService.getBacklogCountForProject(id);
	}

	@RequestMapping(value = "/{id}/sprints", method = RequestMethod.GET)
	@ResponseBody
	public Collection<Sprint> listSprintsForProject(@PathVariable("id") long id) {
		return scrumService.getAllSprintsForProject(id);
	}

	@RequestMapping(value = "/{id}/sprints", method = RequestMethod.GET, params = {
			"first", "max" })
	@ResponseBody
	public Collection<Sprint> listSprintsForProject(
			@PathVariable("id") long id, @RequestParam("first") int first,
			@RequestParam("max") int max) {
		return scrumService.getSprintsForProject(id, first, max);
	}

	@RequestMapping(value = "/{id}/sprints", method = RequestMethod.GET, params = { "count" })
	@ResponseBody
	public Long getSprintCountForProject(@PathVariable("id") long id) {
		return scrumService.getSprintCountForProject(id);
	}

}
