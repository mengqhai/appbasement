package com.workstream.rest.controller;

import org.activiti.engine.identity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.workstream.core.service.UserService;
import com.workstream.rest.RestConstants;
import com.workstream.rest.model.UserRequest;
import com.workstream.rest.model.UserResponse;

@RestController
@RequestMapping(value = RestConstants.REST_ROOT + "/users", headers = "Accept=application/json")
public class UserController {

	@Autowired
	private UserService uSer;

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public UserResponse createUser(@RequestBody UserRequest uReq) {
		User user = uSer.createUser(uReq.getEmail(), uReq.getFirstName(),
				uReq.getPassword());
		UserResponse resp = new UserResponse(user);
		return resp;
	}

}
