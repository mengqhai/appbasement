package com.workstream.rest.controller;

import static com.workstream.rest.utils.RestUtils.decodeUserId;

import java.util.List;
import java.util.Map;

import org.activiti.engine.history.HistoricFormProperty;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.workstream.core.exception.ResourceNotFoundException;
import com.workstream.core.model.Project;
import com.workstream.core.service.CoreFacadeService;
import com.workstream.rest.RestConstants;
import com.workstream.rest.model.ArchFormPropertyResponse;
import com.workstream.rest.model.ArchProcessResponse;
import com.workstream.rest.model.ArchTaskResponse;
import com.workstream.rest.model.AttachmentResponse;
import com.workstream.rest.model.EventResponse;
import com.workstream.rest.model.InnerWrapperObj;

@Api(value = "archives")
@RestController
@RequestMapping(value = "/archives", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArchiveController {

	@Autowired
	private CoreFacadeService core;

	@Autowired
	private TaskController taskCtrl;

	@Autowired
	private ProcessController processCtrl;

	@ApiOperation(value = "Retrieves the archived task for a given id")
	@RequestMapping(value = "/tasks/{id}", method = RequestMethod.GET)
	public ArchTaskResponse getArchivedTask(@PathVariable("id") String taskId) {
		HistoricTaskInstance hiTask = core.getProjectService().getArchTask(
				taskId);
		if (hiTask == null) {
			throw new ResourceNotFoundException("No such archived task");
		}

		ArchTaskResponse resp = new ArchTaskResponse(hiTask);
		return resp;
	}

	@ApiOperation(value = "Retrieves the archived task for a given id")
	@RequestMapping(value = "/tasks/{id}/events", method = RequestMethod.GET)
	public List<EventResponse> getArchivedTaskEvents(
			@PathVariable("id") String taskId) {
		return taskCtrl.getTaskEvents(taskId);
	}

	@ApiOperation(value = "Retrieve the attachment list for a task")
	@RequestMapping(value = "/tasks/{id:\\d+}/attachments", method = RequestMethod.GET)
	public List<AttachmentResponse> getTaskAttachments(
			@PathVariable("id") String taskId) {
		return taskCtrl.getTaskAttachments(taskId);
	}

	@ApiOperation(value = "Retrieve the local variables for a task")
	@RequestMapping(value = "/tasks/{id:\\d+}/vars", method = RequestMethod.GET)
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
	public List<ArchTaskResponse> getArchSubTasks(
			@PathVariable("id") String taskId) {
		List<HistoricTaskInstance> hiTasks = core.getProjectService()
				.filterArchSubTasks(taskId);
		return InnerWrapperObj.valueOf(hiTasks, ArchTaskResponse.class);
	}

	@ApiOperation(value = "Retrieve the for properties for a task")
	@RequestMapping(value = "/tasks/{id:\\d+}/form", method = RequestMethod.GET)
	public List<ArchFormPropertyResponse> getArchTaskFormProperties(
			@PathVariable("id") String taskId) {
		List<HistoricFormProperty> formProperties = core.getProcessService()
				.filterArchTaskFormProperties(taskId);
		return InnerWrapperObj.valueOf(formProperties,
				ArchFormPropertyResponse.class);
	}

	@ApiOperation(value = "Retrieves the archived task list in project")
	@RequestMapping(value = "/projects/{projectId}/tasks", method = RequestMethod.GET)
	public List<ArchTaskResponse> getArchTasksInProject(
			@PathVariable("projectId") long projectId) {
		Project project = core.getProjectService().getProject(projectId);
		if (project == null) {
			throw new ResourceNotFoundException("No such project");
		}

		List<HistoricTaskInstance> hiTasks = core.getProjectService()
				.filterArchTask(project);
		return InnerWrapperObj.valueOf(hiTasks, ArchTaskResponse.class);
	}

	@ApiOperation(value = "Retrieves the archived task for a given id")
	@RequestMapping(value = "/processes/{processId}/tasks", method = RequestMethod.GET)
	public List<ArchTaskResponse> getArchTasksInProcess(
			@PathVariable("processId") String processId) {
		List<HistoricTaskInstance> hiTasks = core.getProcessService()
				.filterArchTaskByProcess(processId);
		return InnerWrapperObj.valueOf(hiTasks, ArchTaskResponse.class);
	}

	@ApiOperation(value = "Retrieves the variables for a process")
	@RequestMapping(value = "/processes/{processId}/vars", method = RequestMethod.GET)
	public Map<String, Object> getArchProcessVars(
			@PathVariable("processId") String processId) {
		HistoricProcessInstance hiPi = core.getProcessService()
				.getHiProcessWithVars(processId);
		return hiPi.getProcessVariables();
	}

	@ApiOperation(value = "Retrieve the form properties for a process", notes = "Including the start form properties and the task form properties")
	@RequestMapping(value = "/processes/{id:\\d+}/form", method = RequestMethod.GET)
	public List<ArchFormPropertyResponse> getArchProcessFormProperties(
			@PathVariable("id") String processId) {
		List<HistoricFormProperty> formProperties = core.getProcessService()
				.filterHiProcessFormProperties(processId);
		return InnerWrapperObj.valueOf(formProperties,
				ArchFormPropertyResponse.class);
	}

	@ApiOperation(value = "Retrieve the attachment list for a task")
	@RequestMapping(value = "/processes/{id:\\d+}/attachments", method = RequestMethod.GET)
	public List<AttachmentResponse> getProcessAttachments(
			@PathVariable("id") String processId) {
		return processCtrl.getProcesssAttachments(processId);
	}

	@ApiOperation(value = "Retrieves the archived processes for a given org id")
	@RequestMapping(value = "/orgs/{orgId}/processes", method = RequestMethod.GET)
	public List<ArchProcessResponse> getArchProcessesInOrg(
			@PathVariable("orgId") Long orgId) {
		List<HistoricProcessInstance> hiPiList = core.getProcessService()
				.filterHiProcessByOrg(orgId, true);
		return InnerWrapperObj.valueOf(hiPiList, ArchProcessResponse.class);
	}

	@ApiOperation(value = "Retrieves the archived processes started by a given user id", notes = RestConstants.TEST_USER_ID_INFO)
	@RequestMapping(value = "/users/{userIdBase64}/processes", method = RequestMethod.GET)
	public List<ArchProcessResponse> getArchProcessesByStarter(
			@PathVariable("userIdBase64") String userIdBase64) {
		String userId = decodeUserId(userIdBase64);
		List<HistoricProcessInstance> hiPiList = core.getProcessService()
				.filterHiProcessByStarter(userId, true);
		return InnerWrapperObj.valueOf(hiPiList, ArchProcessResponse.class);
	}

}
