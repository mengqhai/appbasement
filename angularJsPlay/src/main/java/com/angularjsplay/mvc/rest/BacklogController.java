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
import com.angularjsplay.model.Backlog;
import com.angularjsplay.mvc.validation.ValidateOnCreate;
import com.angularjsplay.service.IScrumService;

@Controller
@RequestMapping(value = "/backlogs", headers = "Accept=application/json")
public class BacklogController {

	@Autowired
	IScrumService scrumService;

	public BacklogController() {
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<Backlog> listBacklogs() {
		return scrumService.getAll(Backlog.class);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	@ResponseBody
	public Backlog getBacklog(@PathVariable("id") long id) {
		return scrumService.getById(Backlog.class, id);
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.CREATED)
	@ResponseBody
	public Backlog createBacklog(
			@RequestBody @Validated(ValidateOnCreate.class) Backlog backlog,
			BindingResult bResult) {
		if (bResult.hasErrors()) {
			throw new ScrumValidationException(bResult);
		}
		backlog.setId(null);
		scrumService.saveBacklogWithPartialProject(backlog);
		return backlog;
	}

}
