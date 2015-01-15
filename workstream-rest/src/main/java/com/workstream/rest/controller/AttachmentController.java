package com.workstream.rest.controller;

import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.task.Attachment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.Api;
import com.workstream.core.service.CoreFacadeService;
import com.workstream.rest.utils.RestUtils;

@Api(value = "attachments")
@RestController
@RequestMapping(value = "/attachments")
public class AttachmentController {

	@Autowired
	private CoreFacadeService core;

	@RequestMapping(value = "/{attachmentId}", method = RequestMethod.GET)
	public HttpEntity<byte[]> getAttachment(
			@PathVariable("attachmentId") String attachmentId,
			@ApiIgnore HttpServletResponse response) {
		Attachment attachment = core.getProcessService().getTaskAttachment(
				attachmentId);

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
		byte[] bytes = core.readAttachmentContent(attachmentId);
		return new HttpEntity<byte[]>(bytes, headers);
	}

}
