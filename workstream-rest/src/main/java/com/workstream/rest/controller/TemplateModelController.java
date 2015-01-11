package com.workstream.rest.controller;

import org.activiti.engine.repository.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.workstream.core.exception.ResourceNotFoundException;
import com.workstream.core.service.CoreFacadeService;
import com.workstream.rest.model.ModelResponse;

@Api(value = "template models", description = "Process template model related operations")
@RestController
@RequestMapping(value = "/templatemodels", produces = MediaType.APPLICATION_JSON_VALUE)
public class TemplateModelController {

	@Autowired
	private CoreFacadeService core;

	@ApiOperation("Get a process template model")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ModelResponse getModel(@PathVariable("id") String modelId) {
		Model model = core.getTemplateService().getModel(modelId);
		if (model == null) {
			throw new ResourceNotFoundException("No such model");
		}
		return new ModelResponse(model);
	}

	@ApiOperation("Delete a process template model")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteModel(@PathVariable("id") String modelId) {
		Model model = core.getTemplateService().getModel(modelId);
		if (model == null) {
			throw new ResourceNotFoundException("No such model");
		}
		core.getTemplateService().removeModel(modelId);
	}

}
