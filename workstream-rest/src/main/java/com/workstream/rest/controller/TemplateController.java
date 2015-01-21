package com.workstream.rest.controller;

import java.util.List;
import java.util.Map;

import org.activiti.engine.form.StartFormData;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
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
import com.workstream.core.exception.ResourceNotFoundException;
import com.workstream.core.service.CoreFacadeService;
import com.workstream.rest.model.InnerWrapperObj;
import com.workstream.rest.model.ProcessResponse;
import com.workstream.rest.model.StartFormDataResponse;
import com.workstream.rest.model.TemplateResponse;
import com.workstream.rest.utils.RestUtils;

/**
 * Templates -- deployed process definitions
 * 
 * @author qinghai
 * 
 */
@Api(value = "templates", description = "Template related operations")
@RestController
@RequestMapping(value = "/templates", produces = MediaType.APPLICATION_JSON_VALUE)
public class TemplateController {

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory
			.getLogger(TemplateController.class);

	@Autowired
	private CoreFacadeService core;

	/**
	 * Template id is somehow derived from the process definition's name, so
	 * could be chinese, and the path variable will have encoding issues.
	 * 
	 * http://www.cnblogs.com/zhonghan/p/3339150.html
	 * http://chen.junchang.blog.163.com/blog/static/634451920126143052531/ Need
	 * to change Tomcat's server.xml: &lt;Connector connectionTimeout="20000"
	 * port="8080" protocol="HTTP/1.1" redirectPort="8443"
	 * URIEncoding="UTF-8"/&gt;
	 * 
	 * 
	 * @param templateId
	 * @return
	 */
	@ApiOperation("Get a process template model")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public TemplateResponse getTemplate(@PathVariable("id") String templateId) {
		String decode = templateId;
		decode = RestUtils.decodeIsoToUtf8(templateId);

		ProcessDefinition pd = core.getTemplateService().getProcessTemplate(
				decode);
		if (pd == null) {
			throw new ResourceNotFoundException("No such template");
		}
		return InnerWrapperObj.valueOf(pd, TemplateResponse.class);
	}

	@ApiOperation(value = "Retrieve the running process list of the process template")
	@RequestMapping(method = RequestMethod.GET, value = "/{templateId}/processes")
	public List<ProcessResponse> getProcessesForTemplate(
			@PathVariable("templateId") String templateId) {
		List<ProcessInstance> piList = core.getProcessService()
				.filterProcessByTemplateId(templateId);
		return InnerWrapperObj.valueOf(piList, ProcessResponse.class);
	}

	@ApiOperation(value = "Retrieve the start form data for a template")
	@RequestMapping(method = RequestMethod.GET, value = "/{templateId}/form")
	public StartFormDataResponse getStartFormDataForTemplate(
			@PathVariable("templateId") String templateId) {
		StartFormData formData = core.getProcessService().getStartFormData(
				templateId);
		return new StartFormDataResponse(formData);
	}

	@ApiOperation(value = "Start a process by submitting the form")
	@RequestMapping(method = RequestMethod.POST, value = "/{templateId}/form")
	public ProcessResponse startProcessByForm(
			@PathVariable("templateId") String templateId,
			@RequestBody(required = true) Map<String, String> formProps) {
		ProcessInstance pi = core.startProcessByForm(templateId, formProps);
		return InnerWrapperObj.valueOf(pi, ProcessResponse.class);
	}

	@ApiOperation(value = "Start a process instance for the template")
	@RequestMapping(method = RequestMethod.POST, value = "/{templateId}/processes")
	public ProcessResponse startProcess(
			@PathVariable("templateId") String templateId) {
		ProcessInstance pi = core.getProcessService().startProcess(templateId);
		return InnerWrapperObj.valueOf(pi, ProcessResponse.class);
	}

}
