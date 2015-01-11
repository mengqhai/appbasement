package com.workstream.rest.controller;

import org.activiti.engine.repository.Model;
import org.activiti.workflow.simple.definition.WorkflowDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.workstream.core.exception.BadArgumentException;
import com.workstream.core.exception.ResourceNotFoundException;
import com.workstream.core.service.CoreFacadeService;
import com.workstream.rest.exception.BytesNotFoundException;
import com.workstream.rest.model.ModelResponse;
import com.workstream.rest.model.ModelWorkflowDefRequest;

@Api(value = "template models", description = "Process template model related operations")
@RestController
@RequestMapping(value = "/templatemodels", produces = MediaType.APPLICATION_JSON_VALUE)
public class TemplateModelController {

	private static final Logger log = LoggerFactory
			.getLogger(TemplateModelController.class);

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

	@ApiOperation("Get the workflow definition of a process template model")
	@RequestMapping(value = "/{id}/workflow", method = RequestMethod.GET)
	public WorkflowDefinition getModelWorkflowDefinition(
			@PathVariable("id") String modelId) {
		return core.getTemplateService().getModelWorkflowDef(modelId);
	}

	@ApiOperation(value = "Update the workflow definition of a process template model", notes = "You may need a json editor: <br/>"
			+ "<a target='_blank' href='http://www.jsoneditoronline.org/'>Editor1</a><br/>"
			+ "<a target='_blank' href='http://codebeautify.org/online-json-editor'>Editor2</a><br/>")
	@RequestMapping(value = "/{id}/workflow", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateWorkflowDefinition(
			@PathVariable("id") String modelId,
			@ApiParam(required = true) @RequestBody ModelWorkflowDefRequest workflow) {
		if (workflow.getWorkflow() == null) {
			throw new BadArgumentException("No workflow definition provided");
		}
		String userId = core.getAuthUserId();
		// TODO may need to check the user's org
		core.getTemplateService().updateModel(modelId, workflow.getWorkflow(),
				workflow.getComment());
		log.info("Model workflow updated by {}", userId);
	}

	@ApiOperation("Get the workflow definition of a process template model")
	@RequestMapping(value = "/{id}/diagram", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
	public byte[] getModelWorkflowDiagram(@PathVariable("id") String modelId)
			throws BytesNotFoundException {
		byte[] bytes = core.getTemplateService().getModelDiagram(modelId);
		if (bytes == null) {
			throw new BytesNotFoundException("No such diagram");
		}
		return bytes;
	}

}
