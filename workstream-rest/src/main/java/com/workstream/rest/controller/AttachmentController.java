package com.workstream.rest.controller;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.task.Attachment;
import org.h2.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.Api;
import com.workstream.core.service.CoreFacadeService;

@Api(value = "attachments")
@Controller
@RequestMapping(value = "/attachments")
public class AttachmentController {

	@Autowired
	private CoreFacadeService core;

	@RequestMapping(value = "/{attachmentId}", method = RequestMethod.GET)
	public void getAttachment(
			@PathVariable("attachmentId") String attachmentId,
			@ApiIgnore HttpServletResponse response) {
		Attachment attachment = core.getProcessService().getTaskAttachment(
				attachmentId);

		try {
			InputStream content = core.getProcessService()
					.getTaskAttachmentContent(attachmentId);
			IOUtils.copy(content, response.getOutputStream());
			response.setContentType(attachment.getType());
			response.setHeader("Content-Disposition", "attachment; filename="
					+ attachment.getName());

			System.out.println(response.getContentType());
			System.out.println(response.getHeader("Content-Disposition"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
