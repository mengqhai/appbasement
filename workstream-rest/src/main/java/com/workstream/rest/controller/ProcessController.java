package com.workstream.rest.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.workstream.core.exception.AttempBadStateException;
import com.workstream.core.exception.BadArgumentException;
import com.workstream.core.exception.BytesNotFoundException;
import com.workstream.core.exception.DataPersistException;
import com.workstream.core.exception.ResourceNotFoundException;
import com.workstream.core.model.CoreEvent.TargetType;
import com.workstream.core.model.Subscription;
import com.workstream.core.service.CoreFacadeService;
import com.workstream.core.service.ProcessService.UserProcessRole;
import com.workstream.rest.RestConstants;
import com.workstream.rest.model.ArchProcessResponse;
import com.workstream.rest.model.AttachmentResponse;
import com.workstream.rest.model.InnerWrapperObj;
import com.workstream.rest.model.ProcessResponse;
import com.workstream.rest.model.SingleValueResponse;
import com.workstream.rest.model.SubscriptionResponse;
import com.workstream.rest.model.TaskResponse;
import com.workstream.rest.utils.RestUtils;

@Api(value = "processes", description = "Process related operations")
@RestController
@RequestMapping(value = "/processes", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProcessController {

	private final static Logger log = LoggerFactory
			.getLogger(ProcessController.class);

	@Autowired
	private CoreFacadeService core;

	@ApiOperation(value = "Retrieve a running process")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForProcess(#processId)")
	public ProcessResponse getProcess(@PathVariable("id") String processId) {
		ProcessInstance pi = core.getProcessService().getProcess(processId);
		if (pi == null) {
			throw new ResourceNotFoundException("No such process, archived?");
		}
		return new ProcessResponse(pi);
	}

	@ApiOperation(value = "Retrieve the archive entry of a running process")
	@RequestMapping(value = "/{id}/archive", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForProcess(#processId)")
	public ArchProcessResponse getProcessArchive(
			@PathVariable("id") String processId) {
		HistoricProcessInstance arch = core.getProcessService().getHiProcess(
				processId);
		return new ArchProcessResponse(arch);
	}

	@ApiOperation(value = "Retrieve the diagram of a running process")
	@RequestMapping(value = "/{id}/diagram", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForProcess(#processId)")
	public void getProcessDiagram(@PathVariable("id") String processId,
			@ApiIgnore HttpServletResponse response) {
		InputStream is = core.getProcessService().generateProcessDiagram(
				processId);
		if (is == null) {
			throw new ResourceNotFoundException("No such process, archived?");
		}
		try {
			IOUtils.copy(is, response.getOutputStream());
			response.setContentType(MediaType.IMAGE_PNG_VALUE);
		} catch (IOException e) {
			throw new BytesNotFoundException(e.getMessage(), e);
		}
	}

	@ApiOperation(value = "Delete a running process")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@PreAuthorize("isAuthInOrgForProcess(#processId)")
	public void deleteProcess(@PathVariable("id") String processId,
			@RequestBody(required = false) String deleteReason) {
		core.getProcessService().removeProcess(processId, deleteReason);
	}

	@ApiOperation(value = "Query the process by user role and userId", notes = RestConstants.TEST_USER_ID_INFO)
	@RequestMapping(method = RequestMethod.GET)
	@PreAuthorize("principal == decodeUserId(#userIdBase64)")
	// only able to see my own
	public List<ArchProcessResponse> getProcessesByUser(
			@RequestParam(required = true) UserProcessRole role,
			@RequestParam(required = true) String userIdBase64,
			@RequestParam(defaultValue = "0") int first,
			@RequestParam(defaultValue = "10") int max) {
		String userId = RestUtils.decodeUserId(userIdBase64);
		List<HistoricProcessInstance> hiList = core.getProcessService()
				.filterHiProcessByUser(role, userId, false, first, max);
		return InnerWrapperObj.valueOf(hiList, ArchProcessResponse.class);
	}

	@ApiOperation(value = "Retrieve running processes started by the current user", notes = "Note: The returned result is a list of <b>history process objects</b>")
	@RequestMapping(value = "/_startedByMe", method = RequestMethod.GET)
	public List<ArchProcessResponse> getProcessesStartedByMe(
			@RequestParam(defaultValue = "0") int first,
			@RequestParam(defaultValue = "10") int max) {
		String userId = core.getAuthUserId();
		List<HistoricProcessInstance> hiList = core.getProcessService()
				.filterHiProcessByStarter(userId, false, first, max);
		return InnerWrapperObj.valueOf(hiList, ArchProcessResponse.class);
	}

	@ApiOperation(value = "Query the process count started by the current user")
	@RequestMapping(value = "/_startedByMe/_count", method = RequestMethod.GET)
	public SingleValueResponse countProcessesStartedByMe() {
		String userId = core.getAuthUserId();
		long count = core.getProcessService().countHiProcessByUser(
				UserProcessRole.STARTER, userId, false);
		return new SingleValueResponse(count);
	}

	@ApiOperation(value = "Query the process count by user role and userId", notes = RestConstants.TEST_USER_ID_INFO)
	@RequestMapping(value = "/_count", method = RequestMethod.GET)
	@PreAuthorize("principal == decodeUserId(#userIdBase64)")
	public SingleValueResponse countProcessesByUser(
			@RequestParam(required = true) UserProcessRole role,
			@RequestParam(required = true) String userIdBase64) {
		String userId = RestUtils.decodeUserId(userIdBase64);
		long count = core.getProcessService().countHiProcessByUser(role,
				userId, false);
		return new SingleValueResponse(count);
	}

	@ApiOperation(value = "Retrieve running processes involved the current user", notes = "Note: The returned result is a list of <b>history process objects</b>")
	@RequestMapping(value = "/_involvedMe", method = RequestMethod.GET)
	public List<ArchProcessResponse> getProcessesInvolvedMe(
			@RequestParam(defaultValue = "0") int first,
			@RequestParam(defaultValue = "10") int max) {
		String userId = core.getAuthUserId();
		List<HistoricProcessInstance> hiList = core.getProcessService()
				.filterHiProcessByInvolved(userId, false, first, max);
		return InnerWrapperObj.valueOf(hiList, ArchProcessResponse.class);
	}

	@ApiOperation(value = "Query the process count that involves the current user")
	@RequestMapping(value = "/_involvedMe/_count", method = RequestMethod.GET)
	public SingleValueResponse countProcessesInvolvedMe() {
		String userId = core.getAuthUserId();
		long count = core.getProcessService().countHiProcessByUser(
				UserProcessRole.INVOLVED, userId, false);
		return new SingleValueResponse(count);
	}

	@ApiOperation(value = "Retrieve all the variables for the process instance", notes = "This is a security hole to be fixed.")
	@RequestMapping(value = "/{id}/vars", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForProcess(#processId)")
	public Map<String, Object> getProcessVariables(
			@PathVariable("id") String processId) {
		ProcessInstance pi = core.getProcessService().getProcessWithVars(
				processId);
		if (pi == null) {
			throw new ResourceNotFoundException("No such process, archived?");
		}
		return pi.getProcessVariables();
	}

	@ApiOperation(value = "Retrieve the unfinished tasks for a process instance")
	@RequestMapping(value = "/{id}/tasks", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForProcess(#processId)")
	public List<TaskResponse> getProcessTasks(
			@PathVariable("id") String processId,
			@RequestParam(defaultValue = "0", required = false) int first,
			@RequestParam(defaultValue = "" + Integer.MAX_VALUE, required = false) int max) {
		List<Task> tasks = core.getProcessService().filterTaskByProcess(
				processId, first, max);
		return InnerWrapperObj.valueOf(tasks, TaskResponse.class);
	}

	@ApiOperation(value = "Count the unfinished tasks for a process instance")
	@RequestMapping(value = "/{id}/tasks/_count", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForProcess(#processId)")
	public SingleValueResponse countProcessTasks(
			@PathVariable("id") String processId) {
		long count = core.getProcessService().countTaskByProcess(processId);
		return new SingleValueResponse(count);
	}

	@ApiOperation(value = "Retrieve subscription list for a process")
	@RequestMapping(value = "/{id}/subscriptions", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForProcess(#processId)")
	public Collection<SubscriptionResponse> getProcessSubscriptions(
			@PathVariable("id") String processId) {
		Collection<Subscription> subs = core.getEventService()
				.filterSubscription(TargetType.PROCESS, processId);
		return InnerWrapperObj.valueOf(subs, SubscriptionResponse.class);
	}

	/**
	 * 
	 * @param processId
	 * @return
	 * @throws AttempBadStateException
	 *             if user already subscribed it
	 */
	@ApiOperation(value = "Subscribe a process for the current user", notes = RestConstants.TEST_USER_ID_INFO)
	@RequestMapping(value = "/{id}/subscriptions", method = RequestMethod.POST)
	@PreAuthorize("isAuthInOrgForProcess(#processId)")
	public SubscriptionResponse subscribeProcess(
			@PathVariable("id") String processId,
			@RequestParam(required = false) String userIdBase64)
			throws AttempBadStateException {
		String userId = core.getAuthUserId();
		if (userIdBase64 != null && !userIdBase64.isEmpty()) {
			userId = RestUtils.decodeUserId(userIdBase64);
		}
		Subscription sub = core.checkSubscribe(userId, TargetType.PROCESS,
				processId);
		return new SubscriptionResponse(sub);
	}

	@ApiOperation(value = "Retrieve the attachment list for a process")
	@RequestMapping(value = "/{id}/attachments", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForProcess(#processId)")
	public List<AttachmentResponse> getProcesssAttachments(
			@PathVariable("id") String processId) {
		List<Attachment> attachments = core.getAttachmentService()
				.filterProcessAttachment(processId);
		return InnerWrapperObj.valueOf(attachments, AttachmentResponse.class);
	}

	@ApiOperation(value = "Create an attachment for a process")
	@RequestMapping(value = "/{id}/attachments", method = RequestMethod.POST)
	@PreAuthorize("isAuthInOrgForProcess(#processId)")
	public AttachmentResponse createProcessAttachment(
			@PathVariable("id") String processId,
			@ApiParam(required = true) @RequestBody MultipartFile file) {
		if (!file.isEmpty()) {
			String contentType = file.getContentType();

			try {

				String decoded = RestUtils.decodeIsoToUtf8(file
						.getOriginalFilename());
				log.info("File recieved name={} size={} content-type={}",
						decoded, file.getSize(), contentType);
				// here is OK for large file, as commons-fileupload temporarily
				// saves the file on disk

				// problem occurs here for large file, because Activiti reads
				// the stream into an byte[] in memory!
				// I'll replace the attachment with my own implementation in the
				// future.

				ProcessInstance pi = core.getProcessService().getProcess(
						processId);
				StringBuilder desc = new StringBuilder();
				if (pi != null && pi.getTenantId() != null) {
					desc.append(pi.getTenantId());
				}
				desc.append("|");
				desc.append(file.getSize());

				Attachment attachment = core.getAttachmentService()
						.createProcessAttachment(processId,
								file.getContentType(), decoded,
								desc.toString(), file.getInputStream(),
								file.getSize());
				return new AttachmentResponse(attachment);
			} catch (IOException e) {
				throw new DataPersistException(e);
			}
		} else {
			throw new BadArgumentException("Empty file");
		}
	}

}
