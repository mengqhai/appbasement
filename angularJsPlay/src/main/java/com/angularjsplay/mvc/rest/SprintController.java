package com.angularjsplay.mvc.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.angularjsplay.model.Sprint;
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
}
