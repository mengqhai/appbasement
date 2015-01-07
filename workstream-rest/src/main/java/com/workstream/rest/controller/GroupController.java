package com.workstream.rest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.workstream.core.exception.ResourceNotFoundException;
import com.workstream.core.model.GroupX;
import com.workstream.core.service.CoreFacadeService;
import com.workstream.rest.model.GroupRequest;
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

	@ApiOperation(value = "Partially update a group", notes = "Doesn't care whether the proper is in Group or GroupX")
	@RequestMapping(value = "/{groupId}", method = RequestMethod.PATCH)
	public void updateGroup(@PathVariable("groupId") String groupId,
			@ApiParam(required = true) @RequestBody GroupRequest groupReq) {
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

}
