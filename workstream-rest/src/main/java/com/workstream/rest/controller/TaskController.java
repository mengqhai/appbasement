package com.workstream.rest.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.identity.Group;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Event;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.workstream.core.CoreConstants;
import com.workstream.core.exception.AttempBadStateException;
import com.workstream.core.exception.BadArgumentException;
import com.workstream.core.exception.DataPersistException;
import com.workstream.core.exception.ResourceNotFoundException;
import com.workstream.core.model.CoreEvent.TargetType;
import com.workstream.core.model.Subscription;
import com.workstream.core.service.CoreFacadeService;
import com.workstream.rest.model.AttachmentResponse;
import com.workstream.rest.model.CommentResponse;
import com.workstream.rest.model.EventResponse;
import com.workstream.rest.model.InnerWrapperObj;
import com.workstream.rest.model.SubscriptionResponse;
import com.workstream.rest.model.TaskFormDataResponse;
import com.workstream.rest.model.TaskRequest;
import com.workstream.rest.model.TaskResponse;
import com.workstream.rest.utils.RestUtils;

@Api(value = "tasks", description = "Task related operations")
@RestController
@RequestMapping(value = "/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskController {
	private final static Logger log = LoggerFactory
			.getLogger(TaskController.class);

	@Autowired
	private CoreFacadeService core;

	@ApiOperation(value = "Query the tasks assigned to the current user")
	@RequestMapping(value = "/_my", method = RequestMethod.GET)
	public List<TaskResponse> getMyAssigneeTasks() {
		String userId = core.getAuthUserId();
		List<Task> tasks = core.getProjectService()
				.filterTaskByAssignee(userId);
		List<TaskResponse> respList = InnerWrapperObj.valueOf(tasks,
				TaskResponse.class);
		return respList;
	}

	@ApiOperation(value = "Query the tasks created current user")
	@RequestMapping(value = "/_createdByMe", method = RequestMethod.GET)
	public List<TaskResponse> getMyCreatorTasks() {
		String userId = core.getAuthUserId();
		List<Task> tasks = core.getProjectService().filterTaskByCreator(userId);
		List<TaskResponse> respList = InnerWrapperObj.valueOf(tasks,
				TaskResponse.class);
		return respList;
	}

	@ApiOperation(value = "Query the candidate tasks of the current user")
	@RequestMapping(value = "/_myCandidate", method = RequestMethod.GET)
	public Map<String, List<TaskResponse>> getMyCandidateTask() {
		String userId = core.getAuthUserId();
		List<Group> groups = core.getUserService().filterGroupByUser(userId);
		Map<String, List<TaskResponse>> respMap = new HashMap<String, List<TaskResponse>>(
				groups.size());
		for (Group g : groups) {
			List<Task> tasks = core.getProcessService()
					.filterTaskByCandidateGroup(g.getId());
			List<TaskResponse> respList = InnerWrapperObj.valueOf(tasks,
					TaskResponse.class);
			respMap.put(g.getId(), respList);
		}
		return respMap;
	}

	@ApiOperation(value = "Get the task for id")
	@RequestMapping(value = "/{id:\\d+}", method = RequestMethod.GET)
	public TaskResponse getTask(@PathVariable("id") String taskId) {
		Task task = core.getProjectService().getTask(taskId);
		if (task == null) {
			throw new ResourceNotFoundException("No such task, archived?");
		}
		TaskResponse resp = InnerWrapperObj.valueOf(task, TaskResponse.class);
		return resp;
	}

	@ApiOperation(value = "Complete the task", notes = "There's a security hole here: user is able to set any process "
			+ "instance variables by submitting the vars object.")
	@RequestMapping(value = "/{id:\\d+}/_complete", method = RequestMethod.PUT)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void completeTask(@PathVariable("id") String taskId,
			@RequestBody(required = false) Map<String, Object> vars) {
		core.getProcessService().completeTask(taskId, vars);
	}

	@ApiOperation(value = "Claim the task")
	@RequestMapping(value = "/{id:\\d+}/_claim", method = RequestMethod.PUT)
	@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
	public void claimTask(@PathVariable("id") String taskId) {
		String userId = core.getAuthUserId();
		core.getProcessService().claimTask(taskId, userId);
	}

	@ApiOperation(value = "Partially update the task for id")
	@RequestMapping(value = "/{id:\\d+}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateTask(@PathVariable("id") String taskId,
			@ApiParam(required = true) @RequestBody TaskRequest taskReq) {
		core.updateTask(taskId, taskReq.getPropMap());
		log.debug("Updated task {}", taskId);
	}

	@ApiOperation(value = "Delete(cancel) the task for id")
	@RequestMapping(value = "/{id:\\d+}", method = RequestMethod.DELETE)
	public void deleteTask(@PathVariable("id") String taskId) {
		// TODO if it's a task of a running process, trying to delete it will
		// cause activiti exception, so an exception mapping is required
		core.getProjectService().deleteTask(taskId);
		log.debug("Deleted task {}", taskId);
	}

	@ApiOperation(value = "Retrieve all the events for a task")
	@RequestMapping(value = "/{id:\\d+}/events", method = RequestMethod.GET)
	public List<EventResponse> getTaskEvents(@PathVariable("id") String taskId) {
		List<Event> events = core.getProjectService().filterTaskEvent(taskId);
		return InnerWrapperObj.valueOf(events, EventResponse.class);
	}

	@ApiOperation(value = "Retrieve all the comments for a task")
	@RequestMapping(value = "/{id:\\d+}/comments", method = RequestMethod.GET)
	public List<CommentResponse> getTaskComments(
			@PathVariable("id") String taskId) {
		List<Comment> events = core.getProjectService().filterTaskComment(
				taskId);
		return InnerWrapperObj.valueOf(events, CommentResponse.class);
	}

	@ApiOperation(value = "Post a comment for a task")
	@RequestMapping(value = "/{id:\\d+}/comments", method = RequestMethod.POST, consumes = MediaType.TEXT_PLAIN_VALUE)
	public CommentResponse addTaskComment(@PathVariable("id") String taskId,
			@RequestBody(required = true) String message) {
		Comment comment = core.getProjectService().addTaskComment(taskId,
				message);
		return new CommentResponse(comment);
	}

	@ApiOperation(value = "Retrieve the form property definitions for a task")
	@RequestMapping(value = "/{id:\\d+}/form", method = RequestMethod.GET)
	public TaskFormDataResponse getTaskFormProps(
			@PathVariable("id") String taskId) {
		TaskFormData formData = core.getProcessService()
				.getTaskFormData(taskId);
		if (formData == null) {
			throw new ResourceNotFoundException();
		}
		return InnerWrapperObj.valueOf(formData, TaskFormDataResponse.class);
	}

	@ApiOperation(value = "Complete the task by submitting the form for a task")
	@RequestMapping(value = "/{id:\\d+}/form", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void completeTaskByForm(@PathVariable("id") String taskId,
			@RequestBody(required = true) Map<String, String> formProps) {
		core.completeTaskByForm(taskId, formProps);
	}

	@ApiOperation(value = "Retrieve the attachment list for a task")
	@RequestMapping(value = "/{id:\\d+}/attachments", method = RequestMethod.GET)
	public List<AttachmentResponse> getTaskAttachments(
			@PathVariable("id") String taskId) {
		List<Attachment> attachments = core.getAttachmentService()
				.filterTaskAttachment(taskId);
		return InnerWrapperObj.valueOf(attachments, AttachmentResponse.class);
	}

	@ApiOperation(value = "Create an attachment for a task")
	@RequestMapping(value = "/{id:\\d+}/attachments", method = RequestMethod.POST)
	public AttachmentResponse createTaskAttachment(
			@PathVariable("id") String taskId,
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
						.createTaskAttachment(taskId, file.getContentType(),
								decoded,
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

	@ApiOperation(value = "Retrieve subscription list for a task")
	@RequestMapping(value = "/{id}/subscriptions", method = RequestMethod.GET)
	public Collection<SubscriptionResponse> getTaskSubscriptions(
			@PathVariable("id") String taskId) {
		Collection<Subscription> subs = core.getEventService()
				.filterSubscription(TargetType.TASK, taskId);
		return InnerWrapperObj.valueOf(subs, SubscriptionResponse.class);
	}

	/**
	 * 
	 * @param taskId
	 * @return
	 * @throws AttempBadStateException
	 *             if user already subscribed it
	 */
	@ApiOperation(value = "Subscribe a task for the current user")
	@RequestMapping(value = "/{id}/subscriptions", method = RequestMethod.POST)
	public SubscriptionResponse subscribeTask(@PathVariable("id") String taskId)
			throws AttempBadStateException {
		String userId = core.getAuthUserId();
		Subscription sub = core.getEventService().subscribe(userId,
				TargetType.TASK, taskId);
		return new SubscriptionResponse(sub);
	}

	@ApiOperation(value = "Retrieve the local variables of a task")
	@RequestMapping(value = "/{id}/vars", method = RequestMethod.GET)
	public Map<String, Object> getTaskVars(@PathVariable("id") String taskId) {
		return core.getProcessService().getTaskLocalVariables(taskId);
	}

	@ApiOperation(value = "Set the local variables of a task")
	@RequestMapping(value = "/{id}/vars", method = RequestMethod.PUT)
	public void setTaskVars(@PathVariable("id") String taskId,
			@RequestBody(required = true) Map<String, Object> vars) {
		core.getProcessService().setTaskLocalVariables(taskId, vars);
	}

}
