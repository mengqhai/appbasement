package com.angularjsplay.mvc.rest;

import java.util.Collection;

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
import com.angularjsplay.model.Sprint;
import com.angularjsplay.model.Task;
import com.angularjsplay.mvc.validation.ValidateOnCreate;
import com.angularjsplay.mvc.validation.ValidateOnUpdate;
import com.angularjsplay.service.IScrumService;

@Controller
@RequestMapping(value = "/sprints", headers = "Accept=application/json")
public class SprintController {

	@Autowired
	IScrumService scrumService;

	public SprintController() {
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Collection<Sprint> listSprints() {
		return scrumService.getAll(Sprint.class);
	}
	
	@RequestMapping(method = RequestMethod.GET, params = { "first", "max" })
	@ResponseBody
	public Collection<Sprint> listSprints(@RequestParam("first") int first,
			@RequestParam("max") int max) {
		return scrumService.getAll(Sprint.class, first, max);
	}

	@RequestMapping(method = RequestMethod.GET, params = { "count" })
	@ResponseBody
	public Long getSprintCount() {
		return scrumService.getAllCount(Sprint.class);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Sprint getSprint(@PathVariable("id") long id) {
		return scrumService.getById(Sprint.class, id);
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.CREATED)
	@ResponseBody
	public Sprint createSprint(
			@RequestBody @Validated(value = { ValidateOnCreate.class }) Sprint sprint,
			BindingResult bResult) {
		if (bResult.hasErrors()) {
			throw new ScrumValidationException(bResult);
		}
		sprint.setId(null);
		scrumService.createWithIdRef(sprint);
		return sprint;
	}

	@RequestMapping(value = "/{id}", method = { RequestMethod.PUT,
			RequestMethod.PATCH })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateSprint(
			@PathVariable("id") long id,
			@RequestBody @Validated(value = { ValidateOnUpdate.class }) Sprint patch,
			BindingResult bResult) {
		if (bResult.hasErrors()) {
			throw new ScrumValidationException(bResult);
		}
		patch.setId(id);
		scrumService.updateWithPatch(patch);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteSprint(@PathVariable("id") long id) {
		scrumService.deleteById(Sprint.class, id);
	}

	@RequestMapping(value = "/{id}/backlogs", method = RequestMethod.GET)
	@ResponseBody
	public Collection<Backlog> listBacklogsForSprint(
			@PathVariable("id") long sprintId) {
		return scrumService.getAllBacklogsForSprint(sprintId);
	}

	@RequestMapping(value = "/{id}/backlogs", method = RequestMethod.GET, params = {
			"first", "max" })
	@ResponseBody
	public Collection<Backlog> listBacklogsForSprint(
			@PathVariable("id") long sprintId,
			@RequestParam("first") int first, @RequestParam("max") int max) {
		return scrumService.getBacklogsForSprint(sprintId, first, max);
	}

	@RequestMapping(value = "/{id}/backlogs", method = RequestMethod.GET, params = "count")
	@ResponseBody
	public Long getBacklogCountForSprint(@PathVariable("id") long sprintId) {
		return scrumService.getBacklogCountForSprint(sprintId);
	}
	
	
	@RequestMapping(value = "/{id}/tasks", method = RequestMethod.GET)
	@ResponseBody
	public Collection<Task> listTasksForSprint(
			@PathVariable("id") long sprintId) {
		return scrumService.getAllTasksForSprint(sprintId);
	}

	@RequestMapping(value = "/{id}/tasks", method = RequestMethod.GET, params = {
			"first", "max" })
	@ResponseBody
	public Collection<Task> listTasksForSprint(
			@PathVariable("id") long sprintId,
			@RequestParam("first") int first, @RequestParam("max") int max) {
		return scrumService.getTasksForSprint(sprintId, first, max);
	}

	@RequestMapping(value = "/{id}/tasks", method = RequestMethod.GET, params = { "count" })
	@ResponseBody
	public Long getTaskCountForSprint(@PathVariable("id") long sprintId) {
		return scrumService.getTaskCountForSprint(sprintId);
	}
}
