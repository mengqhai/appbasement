package com.workstream.rest.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.workstream.core.exception.AttempBadStateException;
import com.workstream.core.exception.BadArgumentException;
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
	public ProcessResponse getProcess(@PathVariable("id") String processId) {
		ProcessInstance pi = core.getProcessService().getProcess(processId);
		if (pi == null) {
			throw new ResourceNotFoundException("No such process, archived?");
		}
		return new ProcessResponse(pi);
	}

	@ApiOperation(value = "Query the process by user role and userId", notes = RestConstants.TEST_USER_ID_INFO)
	@RequestMapping(method = RequestMethod.GET)
	public List<ArchProcessResponse> getProcessesByUser(
			@RequestParam(required = true) UserProcessRole role,
			@RequestParam(required = true) String userIdBase64) {
		String userId = RestUtils.decodeUserId(userIdBase64);
		List<HistoricProcessInstance> hiList = core.getProcessService()
				.filterHiProcessByUser(role, userId, false);
		return InnerWrapperObj.valueOf(hiList, ArchProcessResponse.class);
	}

	@ApiOperation(value = "Retrieve running processes started by the current user", notes = "Note: The returned result is a list of <b>history process objects</b>")
	@RequestMapping(value = "/_startedByMe", method = RequestMethod.GET)
	public List<ArchProcessResponse> getProcessesStartedByMe() {
		String userId = core.getAuthUserId();
		List<HistoricProcessInstance> hiList = core.getProcessService()
				.filterHiProcessByStarter(userId, false);
		return InnerWrapperObj.valueOf(hiList, ArchProcessResponse.class);
	}

	@ApiOperation(value = "Retrieve running processes involved the current user", notes = "Note: The returned result is a list of <b>history process objects</b>")
	@RequestMapping(value = "/_involvedMe", method = RequestMethod.GET)
	public List<ArchProcessResponse> getProcessesInvolvedMe() {
		String userId = core.getAuthUserId();
		List<HistoricProcessInstance> hiList = core.getProcessService()
				.filterHiProcessByInvolved(userId, false);
		return InnerWrapperObj.valueOf(hiList, ArchProcessResponse.class);
	}

	@ApiOperation(value = "Retrieve all the variables for the process instance", notes = "This is a security hole to be fixed.")
	@RequestMapping(value = "/{id}/vars", method = RequestMethod.GET)
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
	public List<TaskResponse> getProcessTasks(
			@PathVariable("id") String processId) {
		List<Task> tasks = core.getProcessService().filterTaskByProcess(
				processId);
		return InnerWrapperObj.valueOf(tasks, TaskResponse.class);
	}

	@ApiOperation(value = "Retrieve subscription list for a process")
	@RequestMapping(value = "/{id}/subscriptions", method = RequestMethod.GET)
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
	@ApiOperation(value = "Subscribe a process for the current user")
	@RequestMapping(value = "/{id}/subscriptions", method = RequestMethod.POST)
	public SubscriptionResponse subscribeProcess(
			@PathVariable("id") String processId)
			throws AttempBadStateException {
		String userId = core.getAuthUserId();
		Subscription sub = core.getEventService().subscribe(userId,
				TargetType.PROCESS, processId);
		return new SubscriptionResponse(sub);
	}

	@ApiOperation(value = "Retrieve the attachment list for a process")
	@RequestMapping(value = "/{id}/attachments", method = RequestMethod.GET)
	public List<AttachmentResponse> getProcesssAttachments(
			@PathVariable("id") String processId) {
		List<Attachment> attachments = core.getAttachmentService()
				.filterProcessAttachment(processId);
		return InnerWrapperObj.valueOf(attachments, AttachmentResponse.class);
	}

	@ApiOperation(value = "Create an attachment for a process")
	@RequestMapping(value = "/{id}/attachments", method = RequestMethod.POST)
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

				Attachment attachment = core.getAttachmentService()
						.createProcessAttachment(processId,
								file.getContentType(), decoded,
								"size: " + file.getSize() / 1024L + "KB",
								file.getInputStream(), file.getSize());
				return new AttachmentResponse(attachment);
			} catch (IOException e) {
				throw new DataPersistException(e);
			}
		} else {
			throw new BadArgumentException("Empty file");
		}
	}

}
