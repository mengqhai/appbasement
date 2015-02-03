package com.workstream.rest.controller;

import java.util.Collection;
import java.util.List;

import javax.validation.groups.Default;

import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.workstream.core.exception.AttempBadStateException;
import com.workstream.core.exception.BadArgumentException;
import com.workstream.core.exception.ResourceNotFoundException;
import com.workstream.core.model.Project;
import com.workstream.core.model.Subscription;
import com.workstream.core.model.CoreEvent.TargetType;
import com.workstream.core.service.CoreFacadeService;
import com.workstream.rest.exception.BeanValidationException;
import com.workstream.rest.model.InnerWrapperObj;
import com.workstream.rest.model.ProjectRequest;
import com.workstream.rest.model.ProjectResponse;
import com.workstream.rest.model.SingleValueResponse;
import com.workstream.rest.model.SubscriptionResponse;
import com.workstream.rest.model.TaskRequest;
import com.workstream.rest.model.TaskResponse;
import com.workstream.rest.utils.RestUtils;
import com.workstream.rest.validation.ValidateOnUpdate;

@Api(value = "projects", description = "Project related operations")
@RestController
@RequestMapping(value = "/projects", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProjectController {

	private final static Logger log = LoggerFactory
			.getLogger(ProjectController.class);

	@Autowired
	private CoreFacadeService core;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ProjectResponse getProject(@PathVariable("id") Long projectId) {
		Project proj = core.getProjectService().getProject(projectId);
		if (proj == null) {
			throw new ResourceNotFoundException("No such project");
		}
		return new ProjectResponse(proj);
	}

	@ApiOperation(value = "Partially update a project")
	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	public void updateProject(
			@PathVariable("id") Long projectId,
			@RequestBody @Validated({ Default.class, ValidateOnUpdate.class }) ProjectRequest projectReq,
			BindingResult bResult) {
		if (bResult.hasErrors()) {
			throw new BeanValidationException(bResult);
		}

		if (projectReq.isRemovingName()) {
			throw new BadArgumentException("Name can't be null");
		}

		core.getProjectService().updateProject(projectId,
				projectReq.getPropMap());
	}

	@ApiOperation(value = "Delete a project", notes = "A cascade delete action")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteProject(@PathVariable("id") Long projectId)
			throws ResourceNotFoundException {
		core.deleteProject(projectId);
		log.debug("Project deleted {}", projectId);
	}

	@ApiOperation(value = "Create a task in the project")
	@RequestMapping(value = "/{id}/tasks", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public TaskResponse createTaskInProject(@PathVariable("id") Long projectId,
			@ApiParam(required = true) @RequestBody TaskRequest taskReq) {
		Task task = core.createTaskInProject(projectId, taskReq.getName(),
				taskReq.getDescription(), taskReq.getDueDate(),
				taskReq.getAssignee(), taskReq.getPriority());
		return new TaskResponse(task);
	}

	@ApiOperation(value = "Retrieves the tasks in the project", notes = "If no assigneeId is provided, all the tasks in the project will be retrieved.")
	@RequestMapping(value = "/{id}/tasks", method = RequestMethod.GET)
	public List<TaskResponse> getTasksInProject(
			@PathVariable("id") Long projectId,
			@RequestParam(required = false) String assigneeId,
			@RequestParam(defaultValue = "0") int first,
			@RequestParam(defaultValue = "10") int max)
			throws ResourceNotFoundException {
		List<Task> tasks = null;
		if (assigneeId == null || assigneeId.isEmpty()) {
			tasks = core.getProjectService().filterTask(projectId, first, max);
		} else {
			String userId = RestUtils.decodeUserId(assigneeId);
			tasks = core.getProjectService().filterTask(projectId, userId,
					first, max);
		}

		List<TaskResponse> respList = InnerWrapperObj.valueOf(tasks,
				TaskResponse.class);
		return respList;
	}

	@ApiOperation(value = "Count the tasks in the project", notes = "If no assigneeId is provided, all the tasks in the project will be counted.")
	@RequestMapping(value = "/{id}/tasks/_count", method = RequestMethod.GET)
	public SingleValueResponse countTasksInProject(
			@PathVariable("id") Long projectId,
			@RequestParam(required = false) String assigneeId)
			throws ResourceNotFoundException {
		long count = 0;
		if (assigneeId == null || assigneeId.isEmpty()) {
			count = core.getProjectService().countTask(projectId);
		} else {
			String userId = RestUtils.decodeUserId(assigneeId);
			count = core.getProjectService().countTask(projectId, userId);
		}
		return new SingleValueResponse(count);
	}

	@ApiOperation(value = "Retrieve subscription list for a project")
	@RequestMapping(value = "/{id}/subscriptions", method = RequestMethod.GET)
	public Collection<SubscriptionResponse> getProjectSubscriptions(
			@PathVariable("id") String project) {
		Collection<Subscription> subs = core.getEventService()
				.filterSubscription(TargetType.PROJECT, project);
		return InnerWrapperObj.valueOf(subs, SubscriptionResponse.class);
	}

	/**
	 * 
	 * @param projectId
	 * @return
	 * @throws AttempBadStateException
	 *             if user already subscribed it
	 */
	@ApiOperation(value = "Subscribe a project for the current user")
	@RequestMapping(value = "/{id}/subscriptions", method = RequestMethod.POST)
	public SubscriptionResponse subscribeProject(
			@PathVariable("id") String projectId)
			throws AttempBadStateException {
		String userId = core.getAuthUserId();
		Subscription sub = core.getEventService().subscribe(userId,
				TargetType.PROJECT, projectId);
		return new SubscriptionResponse(sub);
	}

}
