package com.angularjsplay.mvc.rest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.angularjsplay.exception.ScrumValidationException;
import com.angularjsplay.model.Task;
import com.angularjsplay.mvc.validation.ValidateOnCreate;
import com.angularjsplay.mvc.validation.ValidateOnUpdate;
import com.angularjsplay.service.IScrumService;

@Controller
@RequestMapping(value = "/tasks", headers = "Accept=application/json", produces = "application/json")
public class TaskController {

	@Autowired
	IScrumService scrumService;

	public TaskController() {
	}

	@RequestMapping(method = GET)
	@ResponseBody
	public Collection<Task> listTasks() {
		return scrumService.getAll(Task.class);
	}

	@RequestMapping(value = "/{id}", method = GET)
	@ResponseBody
	public Task getTask(@PathVariable("id") long id) {
		return scrumService.getById(Task.class, id);
	}

	@RequestMapping(method = POST, consumes = "application/json")
	@ResponseBody
	@ResponseStatus(CREATED)
	public Task createTask(
			@RequestBody @Validated(value = { ValidateOnCreate.class }) Task task,
			BindingResult bResult) {
		if (bResult.hasErrors()) {
			throw new ScrumValidationException(bResult);
		}
		task.setId(null);
		scrumService.createWithIdRef(task);
		return task;
	}

	@RequestMapping(value = "/{id}", method = { PUT, PATCH }, consumes = "application/json")
	@ResponseStatus(NO_CONTENT)
	public void updateTask(
			@PathVariable("id") long taskId,
			@RequestBody @Validated(value = { ValidateOnUpdate.class }) Task patch,
			BindingResult bResult) {
		if (bResult.hasErrors()) {
			throw new ScrumValidationException(bResult);
		}
		patch.setId(taskId);
		scrumService.updateWithPatch(patch);
	}

}
