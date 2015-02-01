package com.workstream.rest.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.form.StartFormData;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.workstream.core.exception.BytesNotFoundException;
import com.workstream.core.exception.ResourceNotFoundException;
import com.workstream.core.service.CoreFacadeService;
import com.workstream.rest.model.InnerWrapperObj;
import com.workstream.rest.model.ProcessResponse;
import com.workstream.rest.model.SingleValueResponse;
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

	@ApiOperation(value = "Get the diagram image of a template", produces = MediaType.IMAGE_PNG_VALUE)
	@RequestMapping(value = "/{id}/diagram", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
	@ResponseBody
	public void getTemplateDiagram(@PathVariable("id") String templateId,
			@ApiIgnore HttpServletResponse response) {
		String decode = templateId;
		decode = RestUtils.decodeIsoToUtf8(templateId);
		InputStream is = core.getTemplateService().getProcessTemplateDiagram(
				decode);
		try {
			IOUtils.copy(is, response.getOutputStream());
			response.setContentType(MediaType.IMAGE_PNG_VALUE);
		} catch (IOException e) {
			throw new BytesNotFoundException(e.getMessage(), e);
		}
	}

	@ApiOperation(value = "Get the BPMN xml of a template", produces = MediaType.APPLICATION_XML_VALUE)
	@RequestMapping(value = "/{id}/bpmn", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	@ResponseBody
	public void getTemplateBpmn(@PathVariable("id") String templateId,
			@ApiIgnore HttpServletResponse response) {
		String decode = templateId;
		decode = RestUtils.decodeIsoToUtf8(templateId);
		InputStream is = core.getTemplateService().getProcessTemplateBpmn(
				decode);
		try {
			IOUtils.copy(is, response.getOutputStream());
			response.setContentType(MediaType.APPLICATION_XML_VALUE);
		} catch (IOException e) {
			throw new BytesNotFoundException(e.getMessage(), e);
		}
	}

	@ApiOperation(value = "Retrieve the running process list of the process template")
	@RequestMapping(method = RequestMethod.GET, value = "/{templateId}/processes")
	public List<ProcessResponse> getProcessesForTemplate(
			@PathVariable("templateId") String templateId,
			@RequestParam(defaultValue = "0") int first,
			@RequestParam(defaultValue = "10") int max) {
		List<ProcessInstance> piList = core.getProcessService()
				.filterProcessByTemplateId(templateId, first, max);
		return InnerWrapperObj.valueOf(piList, ProcessResponse.class);
	}

	@ApiOperation(value = "Count the running process of the process template")
	@RequestMapping(method = RequestMethod.GET, value = "/{templateId}/processes/_count")
	public SingleValueResponse countProcessesForTemplate(
			@PathVariable("templateId") String templateId) {
		long count = core.getProcessService().countProcessByTemplateId(
				templateId);
		return new SingleValueResponse(count);
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
