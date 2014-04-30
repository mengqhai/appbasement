package com.angularjsplay.mvc.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.angularjsplay.model.Backlog;
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

}
