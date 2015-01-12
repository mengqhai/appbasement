package com.workstream.rest.controller;

import java.io.UnsupportedEncodingException;

import org.activiti.engine.repository.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.workstream.rest.model.InnerWrapperObj;
import com.workstream.rest.model.TemplateResponse;

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
		try {
			decode = new String(templateId.getBytes("ISO-8859-1"), "utf-8");
			log.info("Decoded templateId {}", decode);
		} catch (UnsupportedEncodingException e) {
			log.error("Failed to decode templateId: {}", templateId);
		}

		ProcessDefinition pd = core.getTemplateService().getProcessTemplate(
				decode);
		if (pd == null) {
			throw new ResourceNotFoundException("No such template");
		}
		return InnerWrapperObj.valueOf(pd, TemplateResponse.class);
	}

}
