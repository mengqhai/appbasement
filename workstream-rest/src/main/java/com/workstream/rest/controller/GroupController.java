package com.workstream.rest.controller;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.workstream.core.model.GroupX;
import com.workstream.core.service.CoreFacadeService;
import com.workstream.rest.exception.ResourceNotFoundException;
import com.workstream.rest.model.GroupResponse;
import com.workstream.rest.model.UserResponse;

@Api(value = "groups", description = "Group related operations")
@RestController
@RequestMapping(value = "/groups", produces = MediaType.APPLICATION_JSON_VALUE)
public class GroupController {

	@Autowired
	CoreFacadeService core;

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
		List<UserResponse> respList = new ArrayList<UserResponse>(users.size());
		for (User user : users) {
			respList.add(new UserResponse(user));
		}
		return respList;
	}

}
