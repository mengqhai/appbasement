package com.workstream.rest.controller;

import static com.workstream.rest.RestConstants.TEST_USER_ID_INFO;

import java.util.List;
import java.util.Map;

import javax.validation.groups.Default;

import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.workstream.core.exception.BadArgumentException;
import com.workstream.core.exception.DataBadStateException;
import com.workstream.core.exception.ResourceNotFoundException;
import com.workstream.core.model.GroupX;
import com.workstream.core.service.CoreFacadeService;
import com.workstream.rest.exception.BeanValidationException;
import com.workstream.rest.model.GroupRequest;
import com.workstream.rest.model.GroupResponse;
import com.workstream.rest.model.InnerWrapperObj;
import com.workstream.rest.model.SingleValueResponse;
import com.workstream.rest.model.TaskResponse;
import com.workstream.rest.model.UserResponse;
import com.workstream.rest.utils.RestUtils;
import com.workstream.rest.validation.ValidateOnUpdate;

@Api(value = "groups", description = "Group related operations")
@RestController
@RequestMapping(value = "/groups", produces = MediaType.APPLICATION_JSON_VALUE)
public class GroupController {

	private static final Logger log = LoggerFactory
			.getLogger(GroupController.class);

	@Autowired
	private CoreFacadeService core;

	@Autowired
	private RestLoginController login;

	@ApiOperation(value = "Get the detailed group information for a given group id", notes = "The detailed information including the description, createdAt, etc.")
	@RequestMapping(value = "/{groupId}", method = RequestMethod.GET)
	public GroupResponse getGroup(@PathVariable("groupId") String groupId) {
		GroupX groupX = core.getUserService().getGroupX(groupId);
		if (groupX == null) {
			throw new ResourceNotFoundException("No such group");
		}
		Group group = core.getUserService().getGroup(groupId);
		return new GroupResponse(group, groupX);
	}

	@ApiOperation(value = "Get user list for a given group", notes = "The list doesn't contain detailed information like createdAt.")
	@RequestMapping(value = "/{groupId}/users", method = RequestMethod.GET)
	public List<UserResponse> getGroupUsers(
			@PathVariable("groupId") String groupId) {
		List<User> users = core.getUserService().filterUserByGroupId(groupId);
		return InnerWrapperObj.valueOf(users, UserResponse.class);
	}

	@ApiOperation(value = "Count user for a given group")
	@RequestMapping(value = "/{groupId}/users/_count", method = RequestMethod.GET)
	public SingleValueResponse countGroupUsers(
			@PathVariable("groupId") String groupId) {
		long count = core.getUserService().countUserByGroupId(groupId);
		return new SingleValueResponse(count);
	}

	@ApiOperation(value = "Partially update a group", notes = "Doesn't care whether the proper is in Group or GroupX")
	@RequestMapping(value = "/{groupId}", method = RequestMethod.PATCH)
	public void updateGroup(@PathVariable("groupId") String groupId,
			@ApiParam(required = true) @RequestBody @Validated({ Default.class,
					ValidateOnUpdate.class }) GroupRequest groupReq,
			BindingResult bResult) {
		if (bResult.hasErrors()) {
			throw new BeanValidationException(bResult);
		}

		Map<String, Object> props = groupReq.getPropMap();
		if (props.isEmpty()) {
			return;
		}
		GroupX groupX = core.getUserService().getGroupX(groupId);
		if (groupX == null) {
			throw new ResourceNotFoundException("No such group");
		}
		if (groupReq.isGroupXPropSet()) {
			core.getUserService().updateGroupX(groupId, props);
		}
		if (groupReq.isGroupPropSet()) {
			core.getUserService().updateGroup(groupId, props);
		}
	}

	/**
	 * 
	 * @param groupId
	 * @param userIdBase64
	 * @throws ResourceNotFoundException
	 *             if user or group doesn't exist
	 * @throws DataBadStateException
	 *             if user and group are not in the same org, or user already in
	 *             the group
	 * 
	 * @throws BadArgumentException
	 *             if the userIdBase64 is not correct
	 */
	@ApiOperation(value = "Add a user to a group", notes = TEST_USER_ID_INFO)
	@ResponseStatus(value = HttpStatus.CREATED)
	@RequestMapping(value = "/{groupId}/users/{userIdBase64}", method = RequestMethod.PUT)
	public void addUserToGroup(@PathVariable("groupId") String groupId,
			@PathVariable("userIdBase64") String userIdBase64)
			throws ResourceNotFoundException, DataBadStateException,
			BadArgumentException {
		String userId = RestUtils.decodeUserId(userIdBase64);
		core.getUserService().addUserToGroup(userId, groupId);
	}

	/**
	 * 
	 * @param groupId
	 * @param userIdBase64
	 * 
	 * @throws BadArgumentException
	 *             if the userIdBase64 is not correct
	 */
	@ApiOperation(value = "Remove a user from a group", notes = TEST_USER_ID_INFO)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@RequestMapping(value = "/{groupId}/users/{userIdBase64}", method = RequestMethod.DELETE)
	public void removeUserFromGroup(@PathVariable("groupId") String groupId,
			@PathVariable("userIdBase64") String userIdBase64)
			throws BadArgumentException {
		String userId = RestUtils.decodeUserId(userIdBase64);
		core.getUserService().removeUserFromGroup(userId, groupId);
		// need to kick the user out
		log.info("Kicking user {} off", userId);
		login.kickOutUserById(userId);
	}

	@ApiOperation(value = "Get candidate tasks for the group", notes = "In other words, get the task(unassigned/claimed) list whose candidate group is "
			+ "the speicified one.")
	@RequestMapping(value = "/{groupId}/candidateTasks", method = RequestMethod.GET)
	public List<TaskResponse> getTasks(@PathVariable("groupId") String groupId) {
		List<Task> tasks = core.getProcessService().filterTaskByCandidateGroup(
				groupId);
		return InnerWrapperObj.valueOf(tasks, TaskResponse.class);
	}

	@ApiOperation(value = "Count candidate tasks for the group", notes = "In other words, get the task(unassigned/claimed) list whose candidate group is "
			+ "the speicified one.")
	@RequestMapping(value = "/{groupId}/candidateTasks/_count", method = RequestMethod.GET)
	public SingleValueResponse countTasksForGroup(
			@PathVariable("groupId") String groupId) {
		long count = core.getProcessService()
				.countTaskByCandidateGroup(groupId);
		return new SingleValueResponse(count);
	}

}
