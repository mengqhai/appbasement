package com.workstream.rest.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.task.Attachment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.Api;
import com.workstream.core.exception.BytesNotFoundException;
import com.workstream.core.exception.ResourceNotFoundException;
import com.workstream.core.model.BinaryObj;
import com.workstream.core.service.CoreFacadeService;
import com.workstream.rest.exception.DataMappingException;
import com.workstream.rest.utils.RestUtils;

@Api(value = "attachments")
@RestController
@RequestMapping(value = "/attachments")
public class AttachmentController {

	@Autowired
	private CoreFacadeService core;

	@RequestMapping(value = "/{attachmentId}/thumb", method = RequestMethod.GET)
	@ResponseBody
	public void getAttachmentThumbnail(
			@PathVariable("attachmentId") String attachmentId,
			@ApiIgnore HttpServletResponse response) {
		Attachment attachment = core.getAttachmentService().getTaskAttachment(
				attachmentId);
		if (attachment == null) {
			throw new ResourceNotFoundException("No such attachment");
		}

		if (attachment.getType() == null
				|| !attachment.getType().startsWith("image")) {
			throw new BytesNotFoundException("No such thumbnail");
		}

		response.setContentType(attachment.getType());
		BinaryObj binary = core.getAttachmentService()
				.getAttachmentThumbBinaryObj(attachment.getId());
		if (binary == null) {
			throw new BytesNotFoundException("No such thumbnail");
		}
		try {
			core.getAttachmentService().outputBinaryObjContent(
					response.getOutputStream(), binary);
		} catch (IOException e) {
			throw new DataMappingException(e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/{attachmentId}/content", method = RequestMethod.GET)
	@ResponseBody
	public void getAttachmentContent(
			@PathVariable("attachmentId") String attachmentId,
			@ApiIgnore HttpServletResponse response) {
		Attachment attachment = core.getAttachmentService().getTaskAttachment(
				attachmentId);
		if (attachment == null) {
			throw new BytesNotFoundException("No such attachment");
		}

		// encountered the header not set issue
		// see
		// http://stackoverflow.com/questions/23525070/httpservletresponse-addheader-and-setheader-not-working-in-spring-controller

		// InputStream content = core.getProcessService()
		// .getTaskAttachmentContent(attachmentId);
		// HttpHeaders headers = new HttpHeaders();
		// headers.add("Content-Type", attachment.getType());
		// headers.add(
		// "Content-Disposition",
		// "attachment; filename="
		// + RestUtils.decodeUtf8ToIso(attachment.getName()));
		try {
			response.setContentType(attachment.getType());
			response.setHeader("Content-Disposition", "attachment; filename="
					+ RestUtils.decodeUtf8ToIso(attachment.getName()));
			BinaryObj binary = core.getAttachmentService()
					.getAttachmentBinaryObj(attachmentId);
			if (binary == null) {
				throw new BytesNotFoundException(
						"No such content for the attachment");
			}
			response.setContentLength((int) binary.getSize());
			core.getAttachmentService().outputBinaryObjContent(
					response.getOutputStream(), binary);
		} catch (IOException e) {
			throw new DataMappingException(e.getMessage(), e);
		}
		// byte[] bytes = new byte[0];
		// return new HttpEntity<byte[]>(bytes, headers);
	}
}
