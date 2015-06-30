package com.workstream.rest.controller;

import static com.workstream.rest.utils.RestUtils.decodeUserId;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.history.HistoricFormProperty;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Event;
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
import com.workstream.core.exception.BadArgumentException;
import com.workstream.core.exception.BytesNotFoundException;
import com.workstream.core.exception.DataPersistException;
import com.workstream.core.exception.ResourceNotFoundException;
import com.workstream.core.model.Project;
import com.workstream.core.service.CoreFacadeService;
import com.workstream.core.service.ProcessService.UserProcessRole;
import com.workstream.core.service.TaskCapable.UserTaskRole;
import com.workstream.rest.RestConstants;
import com.workstream.rest.model.ArchFormPropertyResponse;
import com.workstream.rest.model.ArchProcessResponse;
import com.workstream.rest.model.ArchTaskResponse;
import com.workstream.rest.model.AttachmentResponse;
import com.workstream.rest.model.CommentResponse;
import com.workstream.rest.model.EventResponse;
import com.workstream.rest.model.InnerWrapperObj;
import com.workstream.rest.model.SingleValueResponse;
import com.workstream.rest.model.TaskResponse;
import com.workstream.rest.utils.RestUtils;

@Api(value = "archives")
@RestController
@RequestMapping(value = "/archives", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArchiveController {

	private final static Logger log = LoggerFactory
			.getLogger(ArchiveController.class);

	@Autowired
	private CoreFacadeService core;

	@Autowired
	private ProcessController processCtrl;

	@ApiOperation(value = "Query the tasks by user role and userId", notes = RestConstants.TEST_USER_ID_INFO)
	@RequestMapping(value = "/tasks", method = RequestMethod.GET)
	@PreAuthorize("principal == decodeUserId(#userIdBase64)")
	public List<ArchTaskResponse> getArchTasksByUser(
			@RequestParam(required = true) UserTaskRole role,
			@RequestParam(required = true) String userIdBase64,
			@RequestParam(defaultValue = "0") int first,
			@RequestParam(defaultValue = "10") int max) {
		String userId = RestUtils.decodeUserId(userIdBase64);
		List<HistoricTaskInstance> tasks = core.getProjectService()
				.filterArchTaskByUser(role, userId, first, max);
		List<ArchTaskResponse> respList = InnerWrapperObj.valueOf(tasks,
				ArchTaskResponse.class);
		return respList;
	}

	@ApiOperation(value = "Query the tasks assigned to the current user")
	@RequestMapping(value = "/tasks/_my", method = RequestMethod.GET)
	public List<ArchTaskResponse> getMyAssigneeTasks(
			@RequestParam(defaultValue = "0") int first,
			@RequestParam(defaultValue = "10") int max) {
		String userId = core.getAuthUserId();
		List<HistoricTaskInstance> tasks = core
				.getProjectService()
				.filterArchTaskByUser(UserTaskRole.ASSIGNEE, userId, first, max);
		List<ArchTaskResponse> respList = InnerWrapperObj.valueOf(tasks,
				ArchTaskResponse.class);
		return respList;
	}

	@ApiOperation(value = "Count the tasks assigned to the current user")
	@RequestMapping(value = "/tasks/_my/_count", method = RequestMethod.GET)
	public SingleValueResponse countMyAssigneeTasks() {
		String userId = core.getAuthUserId();
		long count = core.getProjectService().countArchTaskByUser(
				UserTaskRole.ASSIGNEE, userId);
		return new SingleValueResponse(count);
	}

	@ApiOperation(value = "Query the tasks created by the current user")
	@RequestMapping(value = "/tasks/_createdByMe", method = RequestMethod.GET)
	public List<ArchTaskResponse> getMyCreatorTasks(
			@RequestParam(defaultValue = "0") int first,
			@RequestParam(defaultValue = "10") int max) {
		String userId = core.getAuthUserId();
		List<HistoricTaskInstance> tasks = core.getProjectService()
				.filterArchTaskByCreator(userId, first, max);
		List<ArchTaskResponse> respList = InnerWrapperObj.valueOf(tasks,
				ArchTaskResponse.class);
		return respList;
	}

	@ApiOperation(value = "Count the tasks created by the current user")
	@RequestMapping(value = "/tasks/_createdByMe/_count", method = RequestMethod.GET)
	public SingleValueResponse countMyCreatorTasks() {
		String userId = core.getAuthUserId();
		long count = core.getProjectService().countArchTaskByUser(
				UserTaskRole.CREATOR, userId);
		return new SingleValueResponse(count);
	}

	@ApiOperation(value = "Query the candidate tasks of the current user")
	@RequestMapping(value = "/tasks/_myCandidate", method = RequestMethod.GET)
	public List<ArchTaskResponse> getMyCandidateTasks(
			@RequestParam(defaultValue = "0") int first,
			@RequestParam(defaultValue = "10") int max) {
		String userId = core.getAuthUserId();
		List<HistoricTaskInstance> tasks = core.getProjectService()
				.filterArchTaskByCandidateUser(userId, first, max);
		List<ArchTaskResponse> respList = InnerWrapperObj.valueOf(tasks,
				ArchTaskResponse.class);
		return respList;
	}

	@ApiOperation(value = "Count the candidate tasks of the current user")
	@RequestMapping(value = "/tasks/_myCandidate/_count", method = RequestMethod.GET)
	public SingleValueResponse countMyCandidateTasks() {
		String userId = core.getAuthUserId();
		long count = core.getProjectService().countArchTaskByUser(
				UserTaskRole.CANDIDATE, userId);
		return new SingleValueResponse(count);
	}

	@ApiOperation(value = "Count the tasks by user role and userId", notes = RestConstants.TEST_USER_ID_INFO)
	@RequestMapping(value = "/tasks/_count", method = RequestMethod.GET)
	@PreAuthorize("principal == decodeUserId(#userIdBase64)")
	public SingleValueResponse countArchTasksByUser(
			@RequestParam(required = true) UserTaskRole role,
			@RequestParam(required = true) String userIdBase64) {
		String userId = RestUtils.decodeUserId(userIdBase64);
		long count = core.getProjectService().countArchTaskByUser(role, userId);
		return new SingleValueResponse(count);
	}

	@ApiOperation(value = "Retrieves the archived task for a given id")
	@RequestMapping(value = "/tasks/{id}", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForArchTask(#taskId)")
	public ArchTaskResponse getArchTask(@PathVariable("id") String taskId) {
		HistoricTaskInstance hiTask = core.getProjectService().getArchTask(
				taskId);
		if (hiTask == null) {
			throw new ResourceNotFoundException("No such archived task");
		}

		ArchTaskResponse resp = new ArchTaskResponse(hiTask);
		return resp;
	}

	@ApiOperation(value = "Post a comment for an archived task")
	@RequestMapping(value = "/tasks/{id:\\d+}/comments", method = RequestMethod.POST, consumes = MediaType.TEXT_PLAIN_VALUE)
	@PreAuthorize("isAuthInOrgForArchTask(#taskId)")
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

	@ApiOperation(value = "Create an attachment for an archived task")
	@RequestMapping(value = "/tasks/{id:\\d+}/attachments", method = RequestMethod.POST)
	@PreAuthorize("isAuthInOrgForArchTask(#taskId)")
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
				Attachment attachment = core.addAttachmentToTask(taskId,
						file.getSize(), file.getContentType(),
						file.getOriginalFilename(), file.getInputStream());
				return new AttachmentResponse(attachment);
			} catch (IOException e) {
				throw new DataPersistException(e);
			}
		} else {
			throw new BadArgumentException("Empty file");
		}
	}

	@ApiOperation(value = "Recover an achived standalone task")
	@RequestMapping(value = "/tasks/{id}", method = RequestMethod.PUT)
	@PreAuthorize("isAuthInOrgForArchTask(#taskId)")
	public TaskResponse recoverTask(@PathVariable("id") String taskId) {
		Task task = core.getProjectService().createRecoveryTask(taskId);
		return new TaskResponse(task);
	}

	@ApiOperation(value = "Retrieves the archived task for a given id")
	@RequestMapping(value = "/tasks/{id}/events", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForArchTask(#taskId)")
	public List<EventResponse> getArchTaskEvents(
			@PathVariable("id") String taskId) {
		List<Event> events = core.getProjectService().filterTaskEvent(taskId);
		return InnerWrapperObj.valueOf(events, EventResponse.class);
	}

	@ApiOperation(value = "Retrieve the attachment list for a task")
	@RequestMapping(value = "/tasks/{id:\\d+}/attachments", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForArchTask(#taskId)")
	public List<AttachmentResponse> getArchTaskAttachments(
			@PathVariable("id") String taskId) {
		List<Attachment> attachments = core.getAttachmentService()
				.filterTaskAttachment(taskId);
		return InnerWrapperObj.valueOf(attachments, AttachmentResponse.class);
	}

	@ApiOperation(value = "Retrieve the local variables for a task")
	@RequestMapping(value = "/tasks/{id:\\d+}/vars", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForArchTask(#taskId)")
	public Map<String, Object> getArchTaskVars(@PathVariable("id") String taskId) {
		HistoricTaskInstance hiTask = core.getProjectService()
				.getArchTaskWithVars(taskId);
		if (hiTask == null) {
			throw new ResourceNotFoundException("No such archived task");
		}
		return hiTask.getTaskLocalVariables();
	}

	@ApiOperation(value = "Retrieve the sub tasks for a task")
	@RequestMapping(value = "/tasks/{id:\\d+}/tasks", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForArchTask(#taskId)")
	public List<ArchTaskResponse> getArchSubTasks(
			@PathVariable("id") String taskId,
			@RequestParam(defaultValue = "0") int first,
			@RequestParam(defaultValue = "10") int max) {
		List<HistoricTaskInstance> hiTasks = core.getProjectService()
				.filterArchSubTasks(taskId, first, max);
		return InnerWrapperObj.valueOf(hiTasks, ArchTaskResponse.class);
	}

	@ApiOperation(value = "Retrieve the sub task count for a task")
	@RequestMapping(value = "/tasks/{id:\\d+}/tasks/_count", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForArchTask(#taskId)")
	public SingleValueResponse countArchSubTasks(
			@PathVariable("id") String taskId) {
		long count = core.getProjectService().countArchSubTasks(taskId);
		return new SingleValueResponse(count);
	}

	@ApiOperation(value = "Retrieve the for properties for a task")
	@RequestMapping(value = "/tasks/{id:\\d+}/form", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForArchTask(#taskId)")
	public List<ArchFormPropertyResponse> getArchTaskFormProperties(
			@PathVariable("id") String taskId) {
		List<HistoricFormProperty> formProperties = core.getProcessService()
				.filterArchTaskFormProperties(taskId);
		return InnerWrapperObj.valueOf(formProperties,
				ArchFormPropertyResponse.class);
	}

	@ApiOperation(value = "Retrieves the archived task list in project")
	@RequestMapping(value = "/projects/{projectId}/tasks", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForProject(#projectId)")
	public List<ArchTaskResponse> getArchTasksInProject(
			@PathVariable("projectId") long projectId,
			@RequestParam(defaultValue = "0") int first,
			@RequestParam(defaultValue = "10") int max) {
		Project project = core.getProjectService().getProject(projectId);
		if (project == null) {
			throw new ResourceNotFoundException("No such project");
		}

		List<HistoricTaskInstance> hiTasks = core.getProjectService()
				.filterArchTask(project, first, max);
		return InnerWrapperObj.valueOf(hiTasks, ArchTaskResponse.class);
	}

	@ApiOperation(value = "Count the archived task in project")
	@RequestMapping(value = "/projects/{projectId}/tasks/_count", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForProject(#projectId)")
	public SingleValueResponse countArchTasksInProject(
			@PathVariable("projectId") long projectId) {
		Project project = core.getProjectService().getProject(projectId);
		if (project == null) {
			throw new ResourceNotFoundException("No such project");
		}
		long count = core.getProjectService().countArchTask(project);
		return new SingleValueResponse(count);

	}

	@ApiOperation(value = "Query the process by user role and userId", notes = RestConstants.TEST_USER_ID_INFO)
	@RequestMapping(value = "/processes", method = RequestMethod.GET)
	@PreAuthorize("principal == decodeUserId(#userIdBase64)")
	public List<ArchProcessResponse> getArchProcessByUser(
			@RequestParam(required = true) UserProcessRole role,
			@RequestParam(required = true) String userIdBase64,
			@RequestParam(defaultValue = "0") int first,
			@RequestParam(defaultValue = "10") int max) {
		String userId = RestUtils.decodeUserId(userIdBase64);
		List<HistoricProcessInstance> hiList = core.getProcessService()
				.filterHiProcessByUser(role, userId, true, first, max);
		return InnerWrapperObj.valueOf(hiList, ArchProcessResponse.class);
	}

	@ApiOperation(value = "Retrieve archived processes started by the current user", notes = "Note: The returned result is a list of <b>history process objects</b>")
	@RequestMapping(value = "/processes/_startedByMe", method = RequestMethod.GET)
	public List<ArchProcessResponse> getArchProcessesStartedByMe(
			@RequestParam(defaultValue = "0") int first,
			@RequestParam(defaultValue = "10") int max) {
		String userId = core.getAuthUserId();
		List<HistoricProcessInstance> hiList = core.getProcessService()
				.filterHiProcessByStarter(userId, true, first, max);
		return InnerWrapperObj.valueOf(hiList, ArchProcessResponse.class);
	}

	@ApiOperation(value = "Retrieve archived processes involved the current user", notes = "Note: The returned result is a list of <b>history process objects</b>")
	@RequestMapping(value = "/processes/_involvedMe", method = RequestMethod.GET)
	public List<ArchProcessResponse> getArchProcessesInvolvedMe(
			@RequestParam(defaultValue = "0") int first,
			@RequestParam(defaultValue = "10") int max) {
		String userId = core.getAuthUserId();
		List<HistoricProcessInstance> hiList = core.getProcessService()
				.filterHiProcessByInvolved(userId, true, first, max);
		return InnerWrapperObj.valueOf(hiList, ArchProcessResponse.class);
	}

	@ApiOperation(value = "Query the process count by user role and userId", notes = RestConstants.TEST_USER_ID_INFO)
	@RequestMapping(value = "/processes/_count", method = RequestMethod.GET)
	@PreAuthorize("principal == decodeUserId(#userIdBase64)")
	public SingleValueResponse countArchProcessByUser(
			@RequestParam(required = true) UserProcessRole role,
			@RequestParam(required = true) String userIdBase64) {
		String userId = RestUtils.decodeUserId(userIdBase64);
		long count = core.getProcessService().countHiProcessByUser(role,
				userId, true);
		return new SingleValueResponse(count);
	}

	@ApiOperation(value = "Query the archived process count started by the current user")
	@RequestMapping(value = "/processes/_startedByMe/_count", method = RequestMethod.GET)
	public SingleValueResponse countArchProcessesStartedByMe() {
		String userId = core.getAuthUserId();
		long count = core.getProcessService().countHiProcessByUser(
				UserProcessRole.STARTER, userId, true);
		return new SingleValueResponse(count);
	}

	@ApiOperation(value = "Query the archived process count that involves the current user")
	@RequestMapping(value = "/processes/_involvedMe/_count", method = RequestMethod.GET)
	public SingleValueResponse countArchProcessesInvolvedMe() {
		String userId = core.getAuthUserId();
		long count = core.getProcessService().countHiProcessByUser(
				UserProcessRole.INVOLVED, userId, true);
		return new SingleValueResponse(count);
	}

	@ApiOperation(value = "Retrieves the archived tasks in a given process")
	@RequestMapping(value = "/processes/{processId}/tasks", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForArchProcess(#processId)")
	public List<ArchTaskResponse> getArchTasksInProcess(
			@PathVariable("processId") String processId,
			@RequestParam(defaultValue = "0") int first,
			@RequestParam(defaultValue = "10") int max) {
		List<HistoricTaskInstance> hiTasks = core.getProcessService()
				.filterArchTaskByProcess(processId, first, max);
		return InnerWrapperObj.valueOf(hiTasks, ArchTaskResponse.class);
	}

	@ApiOperation(value = "Count the archived task in a given process")
	@RequestMapping(value = "/processes/{processId}/tasks/_count", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForArchProcess(#processId)")
	public SingleValueResponse countArchTasksInProcess(
			@PathVariable("processId") String processId) {
		long count = core.getProcessService().countArchTaskByProcess(processId);
		return new SingleValueResponse(count);
	}

	@ApiOperation(value = "Retrieves the variables for a process")
	@RequestMapping(value = "/processes/{processId}/vars", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForArchProcess(#processId)")
	public Map<String, Object> getArchProcessVars(
			@PathVariable("processId") String processId) {
		HistoricProcessInstance hiPi = core.getProcessService()
				.getHiProcessWithVars(processId);
		return hiPi.getProcessVariables();
	}

	@ApiOperation(value = "Retrieve the archive entry of a running or finished process")
	@RequestMapping(value = "/processes/{id}", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForArchProcess(#processId)")
	public ArchProcessResponse getArchProcess(
			@PathVariable("id") String processId) {
		HistoricProcessInstance arch = core.getProcessService().getHiProcess(
				processId);
		return new ArchProcessResponse(arch);
	}

	@ApiOperation(value = "Retrieve the diagram of an archived process")
	@RequestMapping(value = "/processes/{id}/diagram", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForArchProcess(#processId)")
	public void getArchProcessDiagram(@PathVariable("id") String processId,
			@ApiIgnore HttpServletResponse response) {

		HistoricProcessInstance arch = core.getProcessService().getHiProcess(
				processId);
		if (arch == null) {
			throw new ResourceNotFoundException("No such process");
		}
		String templateId = arch.getProcessDefinitionId();
		InputStream is = null;
		try {
			is = core.getTemplateService()
					.getProcessTemplateDiagram(templateId);

		} catch (ActivitiObjectNotFoundException ex) {
			throw new ResourceNotFoundException("No such process template: "
					+ templateId);
		}
		if (is == null) {
			throw new ResourceNotFoundException("No such process template: "
					+ templateId);
		}
		try {
			IOUtils.copy(is, response.getOutputStream());
			response.setContentType(MediaType.IMAGE_PNG_VALUE);
		} catch (IOException e) {
			throw new BytesNotFoundException(e.getMessage(), e);
		}
	}

	@ApiOperation(value = "Retrieve the form properties for a process", notes = "Including the start form properties and the task form properties")
	@RequestMapping(value = "/processes/{id:\\d+}/form", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForArchProcess(#processId)")
	public List<ArchFormPropertyResponse> getArchProcessFormProperties(
			@PathVariable("id") String processId) {
		List<HistoricFormProperty> formProperties = core.getProcessService()
				.filterHiProcessFormProperties(processId);
		return InnerWrapperObj.valueOf(formProperties,
				ArchFormPropertyResponse.class);
	}

	@ApiOperation(value = "Retrieve the attachment list for a task")
	@RequestMapping(value = "/processes/{id:\\d+}/attachments", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForArchProcess(#processId)")
	public List<AttachmentResponse> getArchProcessAttachments(
			@PathVariable("id") String processId) {
		return processCtrl.getProcesssAttachments(processId);
	}

	@ApiOperation(value = "Retrieves the archived processes for a given org id")
	@RequestMapping(value = "/orgs/{orgId}/processes", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrg(#orgId)")
	public List<ArchProcessResponse> getArchProcessesInOrg(
			@PathVariable("orgId") Long orgId,
			@RequestParam(defaultValue = "0") int first,
			@RequestParam(defaultValue = "10") int max) {
		List<HistoricProcessInstance> hiPiList = core.getProcessService()
				.filterHiProcessByOrg(orgId, true, first, max);
		return InnerWrapperObj.valueOf(hiPiList, ArchProcessResponse.class);
	}

	@ApiOperation(value = "Retrieves the archived processes for a given org id")
	@RequestMapping(value = "/orgs/{orgId}/processes/_count", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrg(#orgId)")
	public SingleValueResponse countArchProcessesInOrg(
			@PathVariable("orgId") Long orgId) {
		long count = core.getProcessService().countHiProcessByOrg(orgId, true);
		return new SingleValueResponse(count);
	}

	@ApiOperation(value = "Retrieves the archived processes started by a given user id", notes = RestConstants.TEST_USER_ID_INFO)
	@RequestMapping(value = "/users/{userIdBase64}/processes", method = RequestMethod.GET)
	@PreAuthorize("principal == decodeUserId(#userIdBase64)")
	public List<ArchProcessResponse> getArchProcessesByStarter(
			@PathVariable("userIdBase64") String userIdBase64,
			@RequestParam(defaultValue = "0") int first,
			@RequestParam(defaultValue = "10") int max) {
		String userId = decodeUserId(userIdBase64);
		List<HistoricProcessInstance> hiPiList = core.getProcessService()
				.filterHiProcessByStarter(userId, true, first, max);
		return InnerWrapperObj.valueOf(hiPiList, ArchProcessResponse.class);
	}

}
