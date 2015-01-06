package com.workstream.rest.controller;

import javax.servlet.http.HttpSession;

import org.activiti.engine.identity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.workstream.core.exception.BadArgumentException;
import com.workstream.core.service.UserService;
import com.workstream.rest.RestConstants;
import com.workstream.rest.exception.BadStateException;
import com.workstream.rest.exception.ResourceNotFoundException;
import com.workstream.rest.model.UserRequest;
import com.workstream.rest.model.UserResponse;

@Api(value = "/users", description = "User related operations", position = 2)
@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

	private final static Logger log = LoggerFactory
			.getLogger(UserController.class);

	@Autowired
	private UserService uSer;

	@ApiOperation(value = "Create a user, e.g. user registration")
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public UserResponse createUser(
			@ApiParam(required = true) @RequestBody UserRequest uReq,
			@RequestParam String captcha, @ApiIgnore HttpSession session) {
		if (captcha == null || captcha.equals("")) {
			throw new BadArgumentException("No captcha");
		}
		String token = (String) session
				.getAttribute(RestConstants.CAPTCHA_TOKEN);
		if (token == null) {
			throw new BadStateException("Captcha not ready");
		}
		if (!captcha.equalsIgnoreCase(token)) {
			throw new BadArgumentException("Wrong captcha");
		}
		// the user id must be globally unique
		User user = uSer.createUser(uReq.getId(), uReq.getEmail(),
				uReq.getFirstName(), uReq.getPassword());
		UserResponse resp = new UserResponse(user);
		session.removeAttribute(RestConstants.CAPTCHA_TOKEN);
		return resp;
	}

	@ApiOperation(value = "Get the user object for the given user id (base64 encoded)", notes = "test user id: <br/> mqhnow1@sina.com: <b>bXFobm93MUBzaW5hLmNvbQ==</b> "
			+ "<br/>projectTester@sina.com:<b>cHJvamVjdFRlc3RlckBzaW5hLmNvbQ==</b>")
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public UserResponse getUser(@PathVariable("id") String userIdBase64) {
		log.info("Activiti user logged in: {}",
				org.activiti.engine.impl.identity.Authentication
						.getAuthenticatedUserId());
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
