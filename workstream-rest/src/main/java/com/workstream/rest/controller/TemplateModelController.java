package com.workstream.rest.controller;

import java.util.Collection;
import java.util.List;

import javax.validation.groups.Default;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.workflow.simple.definition.WorkflowDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.workstream.core.exception.BadArgumentException;
import com.workstream.core.exception.BytesNotFoundException;
import com.workstream.core.exception.DataBadStateException;
import com.workstream.core.model.Revision;
import com.workstream.core.service.CoreFacadeService;
import com.workstream.rest.exception.BeanValidationException;
import com.workstream.rest.model.InnerWrapperObj;
import com.workstream.rest.model.ModelRequest;
import com.workstream.rest.model.ModelResponse;
import com.workstream.rest.model.ModelWorkflowDefRequest;
import com.workstream.rest.model.RevisionResponse;
import com.workstream.rest.model.SingleValueResponse;
import com.workstream.rest.model.TemplateResponse;
import com.workstream.rest.validation.ValidateOnUpdate;

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
	@PreAuthorize("isAuthInOrgForModel(#modelId)")
	public ModelResponse getModel(@PathVariable("id") String modelId) {
		Model model = core.getModel(modelId);
		return new ModelResponse(model);
	}

	@SuppressWarnings("unused")
	@ApiOperation("Delete a process template model")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@PreAuthorize("isAuthProcessDesignerForModel(#modelId) or isAuthAdminForModel(#modelId)")
	public void deleteModel(@PathVariable("id") String modelId) {
		Model model = core.getModel(modelId);// existence check
		core.getTemplateService().removeModel(modelId);
	}

	@ApiOperation("Retrieve the deployed process template list of a model")
	@RequestMapping(value = "/{id}/templates", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForModel(#modelId)")
	public List<TemplateResponse> getDeployedTemplatesByModel(
			@PathVariable("id") String modelId,
			@RequestParam(required = false) boolean onlyLatest,
			@RequestParam(defaultValue = "0") int first,
			@RequestParam(defaultValue = "10") int max) {
		// Model model = core.getTemplateService().getModel(modelId);
		// if (model == null) {
		// throw new ResourceNotFoundException("No such model");
		// }
		List<ProcessDefinition> pdList = core
				.getTemplateService()
				.filterProcessTemplateByModelId(modelId, onlyLatest, first, max);
		return InnerWrapperObj.valueOf(pdList, TemplateResponse.class);
	}

	@ApiOperation("Retrieve the deployed process template list of a model")
	@RequestMapping(value = "/{id}/templates/_count", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForModel(#modelId)")
	public SingleValueResponse countDeployedTemplatesByModel(
			@PathVariable("id") String modelId,
			@RequestParam(required = false) boolean onlyLatest) {
		long count = core.getTemplateService().countProcessTemplateByModelId(
				modelId, onlyLatest);
		return new SingleValueResponse(count);
	}

	@ApiOperation("Deploy a process template model")
	@RequestMapping(value = "/{id}/templates", method = RequestMethod.POST)
	@PreAuthorize("isAuthProcessDesignerForModel(#modelId) or isAuthAdminForModel(#modelId)")
	public TemplateResponse deployModel(@PathVariable("id") String modelId) {
		// TODO user authority check
		Deployment deploy = core.getTemplateService().deployModel(modelId);
		ProcessDefinition template = core.getTemplateService()
				.getProcessTemplateByDeployment(deploy.getId());
		if (template == null) {
			log.error(
					"No template after deployment: deploymentId={} modelId={}",
					deploy.getId(), modelId);
			throw new DataBadStateException("No template after deployment");
		}
		return InnerWrapperObj.valueOf(template, TemplateResponse.class);
	}

	@ApiOperation(value = "Undeploy a process template model", notes = "If only undeploy the last deployment of the model, "
			+ "specify <b>onlyLast=true</b>")
	@RequestMapping(value = "/{id}/templates", method = RequestMethod.DELETE)
	@PreAuthorize("isAuthProcessDesignerForModel(#modelId) or isAuthAdminForModel(#modelId)")
	public void undeployModel(@PathVariable("id") String modelId,
			@RequestParam(required = false, value = "onlyLast") boolean onlyLast) {
		if (onlyLast) {
			core.undeployModelOnlyLast(modelId);
		} else {
			core.undeployModelAll(modelId);
		}
	}

	@ApiOperation("Get the workflow definition of a process template model")
	@RequestMapping(value = "/{id}/workflow", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForModel(#modelId)")
	public WorkflowDefinition getModelWorkflowDefinition(
			@PathVariable("id") String modelId) {
		return core.getTemplateService().getModelWorkflowDef(modelId);
	}

	@ApiOperation(value = "Update the workflow definition of a process template model", notes = "You may need a json editor: <br/>"
			+ "<a target='_blank' href='http://www.jsoneditoronline.org/'>Editor1</a><br/>"
			+ "<a target='_blank' href='http://codebeautify.org/online-json-editor'>Editor2</a><br/>"
			+ "Some typical structure for step:<br/>"
			+ "{\"type\":\"human-step\",<br/>"
			+ "\"name\":\"a human step\",<br/>"
			+ "\"assignee\":\"mqhnow1@sina.com\"}")
	@RequestMapping(value = "/{id}/workflow", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("isAuthProcessDesignerForModel(#modelId) or isAuthAdminForModel(#modelId)")
	public void updateWorkflowDefinition(
			@PathVariable("id") String modelId,
			@ApiParam(required = true) @RequestBody @Validated ModelWorkflowDefRequest workflow,
			BindingResult bResult) {
		if (bResult.hasErrors()) {
			throw new BeanValidationException(bResult);
		}

		WorkflowDefinition def = workflow.getWorkflow();
		if (def == null) {
			throw new BadArgumentException("No workflow definition provided");
		}
		String userId = core.getAuthUserId();
		// TODO may need to check the user's org

		core.getTemplateService().updateModel(modelId, def,
				workflow.getComment());
		log.info("Model workflow updated by {}", userId);
	}

	@ApiOperation(value = "Update a template model")
	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	@PreAuthorize("isAuthProcessDesignerForModel(#modelId) or isAuthAdminForModel(#modelId)")
	public void updateModel(@PathVariable("id") String modelId,
			@ApiParam(required = true) @RequestBody @Validated({ Default.class,
					ValidateOnUpdate.class }) ModelRequest modelReq,
			BindingResult bResult) {

		if (bResult.hasErrors()) {
			throw new BeanValidationException(bResult);
		}

		core.getTemplateService().updateModel(modelId, modelReq.getPropMap());
	}

	@ApiOperation("Get the workflow definition of a process template model")
	@RequestMapping(value = "/{id}/diagram", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
	@PreAuthorize("isAuthInOrgForModel(#modelId)")
	public byte[] getModelWorkflowDiagram(@PathVariable("id") String modelId)
			throws BytesNotFoundException {
		byte[] bytes = core.getTemplateService().getModelDiagram(modelId);
		if (bytes == null) {
			throw new BytesNotFoundException("No such diagram");
		}
		return bytes;
	}

	@ApiOperation("Get the revision list of a process template model")
	@RequestMapping(value = "/{id}/revisions", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForModel(#modelId)")
	public List<RevisionResponse> getRevisions(
			@PathVariable("id") String modelId) {
		Collection<Revision> revisions = core.getTemplateService()
				.filterModelRevision(modelId);
		return InnerWrapperObj.valueOf(revisions, RevisionResponse.class);
	}

}
