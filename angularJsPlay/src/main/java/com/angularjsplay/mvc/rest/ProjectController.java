package com.angularjsplay.mvc.rest;

import java.lang.reflect.Field;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.angularjsplay.exception.ScrumValidationException;
import com.angularjsplay.model.Project;
import com.angularjsplay.mvc.validation.ValidateOnCreate;
import com.angularjsplay.mvc.validation.ValidateOnUpdate;
import com.angularjsplay.service.IScrumService;
import com.appbasement.component.IObjectPatcher;
import com.appbasement.component.PatchedValue;

@Controller
@RequestMapping(value = "/projects", headers = "Accept=application/json")
public class ProjectController {

	@Autowired
	IObjectPatcher objectPatcher;

	@Autowired
	IScrumService scrumService;

	public ProjectController() {
	}

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
			RequestMethod.PATCH })
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

	@RequestMapping(method = RequestMethod.POST)
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
}
