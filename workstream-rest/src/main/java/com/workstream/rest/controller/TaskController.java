package com.workstream.rest.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.groups.Default;

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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
import com.workstream.core.service.TaskCapable.UserTaskRole;
import com.workstream.rest.RestConstants;
import com.workstream.rest.exception.BeanValidationException;
import com.workstream.rest.model.AttachmentResponse;
import com.workstream.rest.model.CommentResponse;
import com.workstream.rest.model.EventResponse;
import com.workstream.rest.model.InnerWrapperObj;
import com.workstream.rest.model.SingleValueResponse;
import com.workstream.rest.model.SubscriptionResponse;
import com.workstream.rest.model.TaskFormDataResponse;
import com.workstream.rest.model.TaskRequest;
import com.workstream.rest.model.TaskResponse;
import com.workstream.rest.utils.RestUtils;
import com.workstream.rest.validation.ValidateOnCreate;
import com.workstream.rest.validation.ValidateOnUpdate;

@Api(value = "tasks", description = "Task related operations")
@RestController
@RequestMapping(value = "/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskController {
	private final static Logger log = LoggerFactory
			.getLogger(TaskController.class);

	@Autowired
	private CoreFacadeService core;

	@ApiOperation(value = "Query the tasks by user role and userId", notes = RestConstants.TEST_USER_ID_INFO)
	@RequestMapping(method = RequestMethod.GET)
	@PreAuthorize("principal == decodeUserId(#userIdBase64)")
	// only able to see my own
	public List<TaskResponse> getTasksForUser(
			@RequestParam(required = true) UserTaskRole role,
			@RequestParam(required = true) String userIdBase64,
			@RequestParam(defaultValue = "0") int first,
			@RequestParam(defaultValue = "10") int max) {
		String userId = RestUtils.decodeUserId(userIdBase64);
		List<Task> tasks = core.getProjectService().filterTaskByUser(role,
				userId, first, max);
		List<TaskResponse> respList = InnerWrapperObj.valueOf(tasks,
				TaskResponse.class);
		return respList;
	}

	@ApiOperation(value = "Query the tasks assigned to the current user")
	@RequestMapping(value = "/_my", method = RequestMethod.GET)
	public List<TaskResponse> getMyAssigneeTasks(
			@RequestParam(defaultValue = "0") int first,
			@RequestParam(defaultValue = "10") int max) {
		String userId = core.getAuthUserId();
		List<Task> tasks = core.getProjectService().filterTaskByAssignee(
				userId, first, max);
		List<TaskResponse> respList = InnerWrapperObj.valueOf(tasks,
				TaskResponse.class);
		return respList;
	}

	@ApiOperation(value = "Count the tasks assigned to the current user")
	@RequestMapping(value = "/_my/_count", method = RequestMethod.GET)
	public SingleValueResponse countMyAssigneeTasks() {
		String userId = core.getAuthUserId();
		long count = core.getProjectService().countTaskByUser(
				UserTaskRole.ASSIGNEE, userId);
		return new SingleValueResponse(count);
	}

	@ApiOperation(value = "Query the tasks created by the current user")
	@RequestMapping(value = "/_createdByMe", method = RequestMethod.GET)
	public List<TaskResponse> getMyCreatorTasks(
			@RequestParam(defaultValue = "0") int first,
			@RequestParam(defaultValue = "10") int max) {
		String userId = core.getAuthUserId();
		List<Task> tasks = core.getProjectService().filterTaskByCreator(userId,
				first, max);
		List<TaskResponse> respList = InnerWrapperObj.valueOf(tasks,
				TaskResponse.class);
		return respList;
	}

	@ApiOperation(value = "Count the tasks created by the current user")
	@RequestMapping(value = "/_createdByMe/_count", method = RequestMethod.GET)
	public SingleValueResponse countMyCreatorTasks() {
		String userId = core.getAuthUserId();
		long count = core.getProjectService().countTaskByUser(
				UserTaskRole.CREATOR, userId);
		return new SingleValueResponse(count);
	}

	@ApiOperation(value = "Query the candidate tasks of the current user")
	@RequestMapping(value = "/_myCandidate", method = RequestMethod.GET)
	public List<TaskResponse> getMyCandidateTasks(
			@RequestParam(defaultValue = "0") int first,
			@RequestParam(defaultValue = "10") int max) {
		String userId = core.getAuthUserId();
		List<Task> tasks = core.getProjectService().filterTaskByCandidateUser(
				userId, first, max);
		List<TaskResponse> respList = InnerWrapperObj.valueOf(tasks,
				TaskResponse.class);
		return respList;
	}

	@ApiOperation(value = "Count the candidate tasks of the current user")
	@RequestMapping(value = "/_myCandidate/_count", method = RequestMethod.GET)
	public SingleValueResponse countMyCandidateTasks() {
		String userId = core.getAuthUserId();
		long count = core.getProjectService().countTaskByUser(
				UserTaskRole.CANDIDATE, userId);
		return new SingleValueResponse(count);
	}

	@ApiOperation(value = "Query the candidate tasks of the current user with groupId as keys")
	@RequestMapping(value = "/_myCandidateWithGroup", method = RequestMethod.GET)
	public Map<String, List<TaskResponse>> getMyCandidateTasskWithGroup() {
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

	@ApiOperation(value = "Count the candidate tasks of the current user with groupId as keys")
	@RequestMapping(value = "/_myCandidateWithGroup/_count", method = RequestMethod.GET)
	public Map<String, Long> countMyCandidateTasskWithGroup() {
		String userId = core.getAuthUserId();
		List<Group> groups = core.getUserService().filterGroupByUser(userId);
		Map<String, Long> respMap = new HashMap<String, Long>(groups.size());
		for (Group g : groups) {
			long count = core.getProcessService().countTaskByCandidateGroup(
					g.getId());
			respMap.put(g.getId(), count);
		}
		return respMap;
	}

	@ApiOperation(value = "Query the task count of a given userId", notes = RestConstants.TEST_USER_ID_INFO)
	@RequestMapping(value = "/_count", method = RequestMethod.GET)
	@PreAuthorize("principal == decodeUserId(#userIdBase64)")
	public SingleValueResponse countTaskForUser(
			@RequestParam(required = true) UserTaskRole role,
			@RequestParam(required = true) String userIdBase64) {
		String userId = RestUtils.decodeUserId(userIdBase64);
		long count = core.getProjectService().countTaskByUser(role, userId);
		return new SingleValueResponse(count);
	}

	@ApiOperation(value = "Get the task for id")
	@RequestMapping(value = "/{id:\\d+}", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForTask(#taskId)")
	public TaskResponse getTask(@PathVariable("id") String taskId) {
		Task task = core.getProjectService().getTask(taskId);
		if (task == null) {
			throw new ResourceNotFoundException("No such task, archived?");
		}
		TaskResponse resp = InnerWrapperObj.valueOf(task, TaskResponse.class);
		return resp;
	}

	@ApiOperation(value = "Create a task for a given task")
	@RequestMapping(value = "/{id:\\d+}/tasks", method = RequestMethod.POST)
	@PreAuthorize("isAuthInOrgForTask(#taskId)")
	public TaskResponse createSubTask(@PathVariable("id") String taskId,
			@RequestBody(required = true) @Validated({ Default.class,
					ValidateOnCreate.class }) TaskRequest taskReq,
			BindingResult bResult) {
		if (bResult.hasErrors()) {
			throw new BeanValidationException(bResult);
		}

		Task task = core.createSubTask(taskId, taskReq.getName(),
				taskReq.getDescription(), taskReq.getDueDate(),
				taskReq.getAssignee(), taskReq.getPriority());
		return new TaskResponse(task);
	}

	@ApiOperation(value = "Retrieve the sub tasks for a given task")
	@RequestMapping(value = "/{id:\\d+}/tasks", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForTask(#taskId)")
	public List<TaskResponse> getSubTasks(@PathVariable("id") String taskId) {
		List<Task> tasks = core.getProjectService().getSubTasks(taskId);
		return InnerWrapperObj.valueOf(tasks, TaskResponse.class);
	}

	@ApiOperation(value = "Count the sub tasks for a given task")
	@RequestMapping(value = "/{id:\\d+}/tasks/_count", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForTask(#taskId)")
	public SingleValueResponse countSubTasks(@PathVariable("id") String taskId) {
		long count = core.getProjectService().countSubTasks(taskId);
		return new SingleValueResponse(count);
	}

	@ApiOperation(value = "Complete the task", notes = "There's a security hole here: user is able to set any process "
			+ "instance variables by submitting the vars object.")
	@RequestMapping(value = "/{id:\\d+}/_complete", method = RequestMethod.PUT)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@PreAuthorize("isAuthInOrgForTask(#taskId) && isAuthMemberCapableForTaskUpdate(#taskId)")
	public void completeTask(@PathVariable("id") String taskId,
			@RequestBody(required = false) Map<String, Object> vars) {
		core.getProcessService().completeTask(taskId, vars);
	}

	@ApiOperation(value = "Claim the task")
	@RequestMapping(value = "/{id:\\d+}/_claim", method = RequestMethod.PUT)
	@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
	@PreAuthorize("isAuthInOrgForTask(#taskId)")
	public void claimTask(@PathVariable("id") String taskId) {
		String userId = core.getAuthUserId();
		core.getProcessService().claimTask(taskId, userId);
	}

	@ApiOperation(value = "Partially update the task for id")
	@RequestMapping(value = "/{id:\\d+}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("isAuthInOrgForTask(#taskId) && isAuthMemberCapableForTaskUpdate(#taskId)")
	public void updateTask(@PathVariable("id") String taskId,
			@ApiParam(required = true) @RequestBody @Validated({ Default.class,
					ValidateOnUpdate.class }) TaskRequest taskReq,
			BindingResult bResult) {
		if (bResult.hasErrors()) {
			throw new BeanValidationException(bResult);
		}

		core.updateTask(taskId, taskReq.getPropMap());
		log.debug("Updated task {}", taskId);
	}

	@ApiOperation(value = "Delete(cancel) the task for id")
	@RequestMapping(value = "/{id:\\d+}", method = RequestMethod.DELETE)
	@PreAuthorize("isAuthInOrgForTask(#taskId) && isAuthMemberCapableForTaskUpdate(#taskId)")
	public void deleteTask(@PathVariable("id") String taskId) {
		// TODO if it's a task of a running process, trying to delete it will
		// cause activiti exception, so an exception mapping is required
		core.getProjectService().deleteTask(taskId);
		log.debug("Deleted task {}", taskId);
	}

	@ApiOperation(value = "Retrieve all the events for a task")
	@RequestMapping(value = "/{id:\\d+}/events", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForTask(#taskId)")
	public List<EventResponse> getTaskEvents(@PathVariable("id") String taskId) {
		List<Event> events = core.getProjectService().filterTaskEvent(taskId);
		return InnerWrapperObj.valueOf(events, EventResponse.class);
	}

	@ApiOperation(value = "Retrieve all the comments for a task")
	@RequestMapping(value = "/{id:\\d+}/comments", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForTask(#taskId)")
	public List<CommentResponse> getTaskComments(
			@PathVariable("id") String taskId) {
		List<Comment> events = core.getProjectService().filterTaskComment(
				taskId);
		return InnerWrapperObj.valueOf(events, CommentResponse.class);
	}

	@ApiOperation(value = "Post a comment for a task")
	@RequestMapping(value = "/{id:\\d+}/comments", method = RequestMethod.POST, consumes = MediaType.TEXT_PLAIN_VALUE)
	@PreAuthorize("isAuthInOrgForTask(#taskId)")
	public CommentResponse addTaskComment(@PathVariable("id") String taskId,
			@RequestBody(required = true) String message) {
		if (message == null || message.isEmpty()) {
			throw new BadArgumentException("Message can be empty");
		}
		if (message.length() > RestConstants.VALID_COMMENT_MAX_SIZE) {
			throw new BadArgumentException(
					"Message length must smaller then 4000");
		}

		Comment comment = core.getProjectService().addTaskComment(taskId,
				message);
		return new CommentResponse(comment);
	}

	@ApiOperation(value = "Retrieve the form property definitions for a task")
	@RequestMapping(value = "/{id:\\d+}/form", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForTask(#taskId)")
	public TaskFormDataResponse getTaskFormProps(
			@PathVariable("id") String taskId) {
		TaskFormData formData = core.getProcessService()
				.getTaskFormData(taskId);
		if (formData == null) {
			throw new ResourceNotFoundException();
		}
		return InnerWrapperObj.valueOf(formData, TaskFormDataResponse.class);
	}

	@ApiOperation(value = "Complete the task by submitting the form for a task", notes = "The test form variables:<br/>"
			+ "<b>{"
			+ "\"textProp\":\"text value\",<br/>"
			+ "\"numProp\":1235,<br/>"
			+ "\"dateProp\":\"2014-11-16 23:30:15\"<br/>" + "}</b>")
	@RequestMapping(value = "/{id:\\d+}/form", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("isAuthInOrgForTask(#taskId)")
	public void completeTaskByForm(@PathVariable("id") String taskId,
			@RequestBody(required = true) Map<String, String> formProps) {
		core.completeTaskByForm(taskId, formProps);
	}

	@ApiOperation(value = "Retrieve the attachment list for a task")
	@RequestMapping(value = "/{id:\\d+}/attachments", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForTask(#taskId)")
	public List<AttachmentResponse> getTaskAttachments(
			@PathVariable("id") String taskId) {
		List<Attachment> attachments = core.getAttachmentService()
				.filterTaskAttachment(taskId);
		return InnerWrapperObj.valueOf(attachments, AttachmentResponse.class);
	}

	@ApiOperation(value = "Create an attachment for a task")
	@RequestMapping(value = "/{id:\\d+}/attachments", method = RequestMethod.POST)
	@PreAuthorize("isAuthInOrgForTask(#taskId)")
	public AttachmentResponse createTaskAttachment(
			@PathVariable("id") String taskId,
			@ApiParam(required = true) @RequestBody MultipartFile file) {
		if (!file.isEmpty()) {
			String contentType = file.getContentType();
			try {

				String decodedFileName = RestUtils.decodeIsoToUtf8(file
						.getOriginalFilename());
				log.info("File recieved name={} size={} content-type={}",
						decodedFileName, file.getSize(), contentType);
				// here is OK for large file, as commons-fileupload temporarily
				// saves the file on disk

				// problem occurs here for large file, because Activiti reads
				// the stream into an byte[] in memory!
				// I'll replace the attachment with my own implementation in the
				// future.
				Attachment attachment = core.addAttachmentToTask(taskId,
						file.getSize(), file.getContentType(), decodedFileName,
						file.getInputStream());
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
	@PreAuthorize("isAuthInOrgForTask(#taskId)")
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
	@ApiOperation(value = "Subscribe a task for the current user", notes = RestConstants.TEST_USER_ID_INFO)
	@RequestMapping(value = "/{id}/subscriptions", method = RequestMethod.POST)
	@PreAuthorize("isAuthInOrgForTask(#taskId)")
	public SubscriptionResponse subscribeTask(
			@PathVariable("id") String taskId,
			@RequestParam(required = false) String userIdBase64)
			throws AttempBadStateException {
		String userId = core.getAuthUserId();
		if (userIdBase64 != null && !userIdBase64.isEmpty()) {
			userId = RestUtils.decodeUserId(userIdBase64);
		}
		Subscription sub = core.checkSubscribe(userId, TargetType.TASK, taskId);
		return new SubscriptionResponse(sub);
	}

	@ApiOperation(value = "Unsubscribe a task for the current user")
	@RequestMapping(value = "/{id}/subscriptions", method = RequestMethod.DELETE)
	@PreAuthorize("isAuthInOrgForTask(#taskId)")
	public void unsubscribeForCurrentUser(@PathVariable("id") String taskId) {
		core.unsubscribeCurrentUser(TargetType.TASK, taskId);
	}

	@ApiOperation(value = "Count the subscription for a task")
	@RequestMapping(value = "/{id}/subscriptions/_count", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForTask(#taskId)")
	public SingleValueResponse countSubscription(
			@PathVariable("id") String taskId) {
		return new SingleValueResponse(core.countSubscription(TargetType.TASK,
				taskId));
	}

	@ApiOperation(value = "Retrieve the local variables of a task")
	@RequestMapping(value = "/{id}/vars", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForTask(#taskId)")
	public Map<String, Object> getTaskVars(@PathVariable("id") String taskId) {
		return core.getProcessService().getTaskLocalVariables(taskId);
	}

	@ApiOperation(value = "Set the local variables of a task")
	@RequestMapping(value = "/{id}/vars", method = RequestMethod.PUT)
	@PreAuthorize("isAuthInOrgForTask(#taskId)")
	public void setTaskVars(@PathVariable("id") String taskId,
			@RequestBody(required = true) Map<String, Object> vars) {
		core.getProcessService().setTaskLocalVariables(taskId, vars);
	}

}
