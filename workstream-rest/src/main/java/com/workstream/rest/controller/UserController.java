package com.workstream.rest.controller;

import org.activiti.engine.identity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.workstream.core.service.UserService;
import com.workstream.rest.RestConstants;
import com.workstream.rest.exception.BadArgumentException;
import com.workstream.rest.exception.ResourceNotFoundException;
import com.workstream.rest.model.UserRequest;
import com.workstream.rest.model.UserResponse;

@RestController
@RequestMapping(value = RestConstants.REST_ROOT + "/users", produces = "application/json")
public class UserController {

	private final static Logger log = LoggerFactory
			.getLogger(UserController.class);

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

	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public UserResponse getUser(@PathVariable("id") String userIdBase64) {
		// the id field in the url must be encoded in base64
		// browsers can natively encode/decode the id string btoa(idString)
		// atob(stringToDecode)
		byte[] decoded = null;
		try {
			decoded = Base64.decode(userIdBase64.getBytes());
		} catch (Exception e) {
			log.warn("Unable to parse user id from base64: {}", userIdBase64, e);
			throw new BadArgumentException("Bad user id", e);
		}

		String userId = new String(decoded);
		User user = uSer.getUser(userId);
		if (user == null) {
			throw new ResourceNotFoundException("No such user.");
		}
		UserResponse resp = new UserResponse(user);
		return resp;
	}

}
