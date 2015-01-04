package com.workstream.rest.controller;

import org.activiti.engine.identity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.workstream.core.service.UserService;
import com.workstream.rest.model.LoginRequest;
import com.workstream.rest.model.LoginResponse;
import com.workstream.rest.model.UserResponse;

@RestController
public class RestLoginController {

	/**
	 * If expose the AuthenticationManager from WebSecurityConfigurerAdapter,
	 * authentication will somehow encounter the dead loop problem.  So here I
	 * have to wire my own basic provider.
	 */
	@Autowired
	private AuthenticationProvider basicProvider;

	@Autowired
	private UserService uSer;

	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public LoginResponse login(@RequestBody LoginRequest req) {
		String username = req.getUserId();
		String password = req.getPassword();
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
				username, password);
		LoginResponse result = new LoginResponse();
		try {
			Authentication auth = basicProvider.authenticate(token);
			// this will eventually delegate to our BasicAuthenticationProvider
			SecurityContextHolder.getContext().setAuthentication(auth);
			// return the authenticated user info
			User user = uSer.getUser(username);
			UserResponse resp = new UserResponse(user);
			result.setUser(resp);
		} catch (Exception e) {
			result.setSuccess(false);
			result.setFailReason(e.getMessage());
		}
		result.setSuccess(true);
		return result;
	}

}
