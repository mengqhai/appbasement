package com.workstream.rest.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.task.Attachment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.Api;
import com.workstream.core.service.CoreFacadeService;
import com.workstream.rest.exception.DataMappingException;
import com.workstream.rest.utils.RestUtils;

@Api(value = "attachments")
@RestController
@RequestMapping(value = "/attachments")
public class AttachmentController {

	@Autowired
	private CoreFacadeService core;

	@RequestMapping(value = "/{attachmentId}/content", method = RequestMethod.GET)
	@ResponseBody
	public void getAttachmentContent(
			@PathVariable("attachmentId") String attachmentId,
			@ApiIgnore HttpServletResponse response) {
		Attachment attachment = core.getProcessService().getTaskAttachment(
				attachmentId);
		if (attachment == null) {
			throw new ResourceAccessException("No such attachment");
		}

		// encountered the header not set issue
		// see
		// http://stackoverflow.com/questions/23525070/httpservletresponse-addheader-and-setheader-not-working-in-spring-controller

		// InputStream content = core.getProcessService()
		// .getTaskAttachmentContent(attachmentId);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", attachment.getType());
		headers.add(
				"Content-Disposition",
				"attachment; filename="
						+ RestUtils.decodeUtf8ToIso(attachment.getName()));
		try {
			response.setContentType(attachment.getType());
			response.setHeader("Content-Disposition", "attachment; filename="
					+ RestUtils.decodeUtf8ToIso(attachment.getName()));
			core.readAttachmentContentToStream(attachmentId,
					response.getOutputStream());
		} catch (IOException e) {
			throw new DataMappingException();
		}
		// byte[] bytes = new byte[0];
		// return new HttpEntity<byte[]>(bytes, headers);
	}

}
