package com.workstream.rest.controller;

import java.util.Collection;
import java.util.List;

import javax.validation.groups.Default;

import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.workstream.core.model.CoreEvent.TargetType;
import com.workstream.core.model.Project;
import com.workstream.core.model.ProjectMembership;
import com.workstream.core.model.ProjectMembership.ProjectMembershipType;
import com.workstream.core.model.Subscription;
import com.workstream.core.model.TaskList;
import com.workstream.core.service.CoreFacadeService;
import com.workstream.rest.RestConstants;
import com.workstream.rest.exception.BeanValidationException;
import com.workstream.rest.model.InnerWrapperObj;
import com.workstream.rest.model.ProjectMembershipRequest;
import com.workstream.rest.model.ProjectMembershipResponse;
import com.workstream.rest.model.ProjectRequest;
import com.workstream.rest.model.ProjectResponse;
import com.workstream.rest.model.SingleValueResponse;
import com.workstream.rest.model.SubscriptionResponse;
import com.workstream.rest.model.TaskListRequest;
import com.workstream.rest.model.TaskListResponse;
import com.workstream.rest.model.TaskRequest;
import com.workstream.rest.model.TaskResponse;
import com.workstream.rest.utils.RestUtils;
import com.workstream.rest.validation.ValidateOnCreate;
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
	@PreAuthorize("isAuthInOrgForProject(#projectId)")
	public ProjectResponse getProject(@PathVariable("id") Long projectId) {
		Project proj = core.getProjectService().getProject(projectId);
		if (proj == null) {
			throw new ResourceNotFoundException("No such project");
		}
		return new ProjectResponse(proj);
	}

	@RequestMapping(value = "/{id}/memberships", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForProject(#projectId) && isAuthMemberCapableForProjectMemRetrieve(#projectId)")
	public List<ProjectMembershipResponse> getProjectMemberships(
			@PathVariable("id") Long projectId,
			@RequestParam(defaultValue = "0") int first,
			@RequestParam(defaultValue = "10") int max) {
		Collection<ProjectMembership> mems = core.getProjectService()
				.filterProjectMemberships(projectId, first, max);
		List<ProjectMembershipResponse> resp = InnerWrapperObj.valueOf(mems,
				ProjectMembershipResponse.class);
		return resp;
	}

	@RequestMapping(value = "/{id}/memberships/_my", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForProject(#projectId)")
	public ProjectMembershipResponse getMyMembershipForProject(
			@PathVariable("id") Long projectId) {
		String userId = core.getAuthUserId();
		ProjectMembership mem = core.getProjectService().getProjectMembership(
				userId, projectId);
		if (mem == null) {
			return null;
		} else {
			return new ProjectMembershipResponse(mem);
		}
	}

	@RequestMapping(value = "/{id}/memberships", method = RequestMethod.POST)
	@PreAuthorize("isAuthInOrgForProject(#projectId) && isAuthMemberCapableForProjectMemUpdate(#projectId)")
	public ProjectMembershipResponse createProjectMembership(
			@PathVariable("id") Long projectId,
			@RequestBody(required = true) ProjectMembershipRequest membership) {
		String userId = membership.getUserId();
		ProjectMembershipType type = membership.getType();
		ProjectMembership membershipResult = core.getProjectService()
				.checkCreateMembership(projectId, userId, type);
		return new ProjectMembershipResponse(membershipResult);
	}

	@RequestMapping(value = "/{id}/memberships/{memId}", method = RequestMethod.PATCH)
	@PreAuthorize("isAuthInOrgForProject(#projectId) && isAuthMemberCapableForProjectMemUpdate(#projectId)")
	public void updateProjectMembership(@PathVariable("id") Long projectId,
			@PathVariable("memId") Long memId,
			@RequestBody(required = true) ProjectMembershipRequest membership) {
		if (membership.getUserId() != null) {
			throw new BadArgumentException(
					"UserId in membership is not updatable.");
		}
		core.getProjectService().updateProjectMembership(memId,
				membership.getType());

	}

	@RequestMapping(value = "/{id}/memberships/{memId}", method = RequestMethod.DELETE)
	@PreAuthorize("isAuthInOrgForProject(#projectId) && isAuthMemberCapableForProjectMemUpdate(#projectId)")
	public void deleteProjectMembership(@PathVariable("id") Long projectId,
			@PathVariable("memId") Long memId) {
		core.getProjectService().deleteMembership(memId);
	}

	@ApiOperation(value = "Partially update a project")
	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	@PreAuthorize("isAuthInOrgForProject(#projectId) && isAuthMemberCapableForProjectUpdate(#projectId)")
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
	@PreAuthorize("isAuthAdminForProject(#projectId)")
	public void deleteProject(@PathVariable("id") Long projectId)
			throws ResourceNotFoundException {
		core.deleteProject(projectId);
		log.debug("Project deleted {}", projectId);
	}

	@ApiOperation(value = "Create a task in the project")
	@RequestMapping(value = "/{id}/tasks", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("isAuthInOrgForProject(#projectId) && isAuthMemberCapableForTaskUpdate(#projectId)")
	public TaskResponse createTaskInProject(
			@PathVariable("id") Long projectId,
			@ApiParam(required = true) @RequestBody(required = true) @Validated({
					Default.class, ValidateOnCreate.class }) TaskRequest taskReq,
			BindingResult bResult) {
		if (bResult.hasErrors()) {
			throw new BeanValidationException(bResult);
		}

		if (taskReq.getAssignee() != null) {
			if (!core.getProjectService().checkMembershipForTaskUpdate(
					taskReq.getAssignee(), projectId)) {
				throw new AttempBadStateException(
						"User is not allowed to work with the project.");
			}
		}

		Task task = core.createTaskInProject(projectId, taskReq.getName(),
				taskReq.getDescription(), taskReq.getDueDate(),
				taskReq.getAssignee(), taskReq.getPriority());
		return new TaskResponse(task);
	}

	@ApiOperation(value = "Create a task list in the project")
	@RequestMapping(value = "/{id}/tasklists", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("isAuthInOrgForProject(#projectId) && isAuthMemberCapableForTaskUpdate(#projectId)")
	public TaskListResponse createTaskListInProject(
			@PathVariable("id") Long projectId,
			@ApiParam(required = true) @RequestBody(required = true) @Validated({
					Default.class, ValidateOnCreate.class }) TaskListRequest taskListReq,
			BindingResult bResult) {
		if (bResult.hasErrors()) {
			throw new BeanValidationException(bResult);
		}

		TaskList taskList = core.getProjectService().createTaskList(projectId,
				taskListReq.getPropMap());
		return new TaskListResponse(taskList);
	}

	@ApiOperation(value = "Retrieve all the task lists in the project")
	@RequestMapping(value = "/{id}/tasklists", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForProject(#projectId) && isAuthMemberCapableForTaskRetrieve(#projectId)")
	public List<TaskListResponse> getTaskListsInProject(
			@PathVariable("id") Long projectId) {
		Collection<TaskList> taskLists = core.getProjectService()
				.filterTaskList(projectId);
		return InnerWrapperObj.valueOf(taskLists, TaskListResponse.class);
	}

	@ApiOperation(value = "Retrieves the tasks in the project", notes = "If no assigneeId is provided, all the tasks in the project will be retrieved.")
	@RequestMapping(value = "/{id}/tasks", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForProject(#projectId) && isAuthMemberCapableForTaskRetrieve(#projectId)")
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

	@RequestMapping(value = "/{id}/tasks/_noParent", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForProject(#projectId) && isAuthMemberCapableForTaskRetrieve(#projectId)")
	public List<TaskResponse> getTasksInProjectNoParent(
			@PathVariable("id") Long projectId,
			@RequestParam(defaultValue = "0") int first,
			@RequestParam(defaultValue = "10") int max) {
		List<Task> tasks = core.getProjectService().filterTaskWithNoParent(
				projectId, first, max);
		List<TaskResponse> respList = InnerWrapperObj.valueOf(tasks,
				TaskResponse.class);
		return respList;
	}

	@ApiOperation(value = "Count the tasks in the project", notes = "If no assigneeId is provided, all the tasks in the project will be counted.")
	@RequestMapping(value = "/{id}/tasks/_count", method = RequestMethod.GET)
	@PreAuthorize("isAuthInOrgForProject(#projectId)  && isAuthMemberCapableForTaskRetrieve(#projectId)")
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
	@PreAuthorize("isAuthInOrgForProject(#projectId)")
	public Collection<SubscriptionResponse> getProjectSubscriptions(
			@PathVariable("id") String projectId) {
		Collection<Subscription> subs = core.getEventService()
				.filterSubscription(TargetType.PROJECT, projectId);
		return InnerWrapperObj.valueOf(subs, SubscriptionResponse.class);
	}

	/**
	 * 
	 * @param projectId
	 * @return
	 * @throws AttempBadStateException
	 *             if user already subscribed it
	 */
	@ApiOperation(value = "Subscribe a project for the current user", notes = RestConstants.TEST_USER_ID_INFO)
	@RequestMapping(value = "/{id}/subscriptions", method = RequestMethod.POST)
	@PreAuthorize("isAuthInOrgForProject(#projectId)")
	public SubscriptionResponse subscribeProject(
			@PathVariable("id") String projectId,
			@RequestParam(required = false) String userIdBase64)
			throws AttempBadStateException {
		String userId = core.getAuthUserId();
		if (userIdBase64 != null && !userIdBase64.isEmpty()) {
			userId = RestUtils.decodeUserId(userIdBase64);
		}

		Subscription sub = core.checkSubscribe(userId, TargetType.PROJECT,
				projectId);
		return new SubscriptionResponse(sub);
	}

}
