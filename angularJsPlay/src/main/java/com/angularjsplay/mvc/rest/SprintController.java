package com.angularjsplay.mvc.rest;

import java.util.List;

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
import com.angularjsplay.model.Sprint;
import com.angularjsplay.mvc.validation.ValidateOnCreate;
import com.angularjsplay.mvc.validation.ValidateOnPartial;
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
	public List<Sprint> listSprints() {
		return scrumService.getAll(Sprint.class);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Sprint getSprint(@PathVariable("id") long id) {
		return scrumService.getById(Sprint.class, id);
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.CREATED)
	@ResponseBody
	public Sprint createSprint(@RequestBody @Validated(value = {
			ValidateOnCreate.class, ValidateOnPartial.class }) Sprint sprint,
			BindingResult bResult) {
		if (bResult.hasErrors()) {
			throw new ScrumValidationException(bResult);
		}
		sprint.setId(null);
		scrumService.saveSprintWithPartialProject(sprint);
		return sprint;
	}
}
