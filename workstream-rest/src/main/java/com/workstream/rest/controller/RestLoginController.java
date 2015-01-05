package com.workstream.rest.controller;

import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.identity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;
import com.workstream.core.service.UserService;
import com.workstream.rest.RestConstants;
import com.workstream.rest.model.LoginRequest;
import com.workstream.rest.model.LoginResponse;
import com.workstream.rest.model.UserResponse;

@RestController
public class RestLoginController {

	/**
	 * If expose the AuthenticationManager from WebSecurityConfigurerAdapter,
	 * authentication will somehow encounter the dead loop problem. So here I
	 * have to wire my own basic provider.
	 */
	@Autowired
	private AuthenticationProvider basicProvider;

	@Autowired
	private UserService uSer;

	protected String generateRestToken(String authName) {
		String org = authName + System.currentTimeMillis();
		String token = DigestUtils.md5DigestAsHex(org.getBytes());
		return token;
	}

	@ApiOperation(value = "A login operation, this operation will generate an api_token that the client can further use.", notes = "Try login the test user: <br/>"
			+ "{ <br/>"
			+ "\"password\": \"passw0rd\",<br/>"
			+ "\"userId\": \"mqhnow1@sina.com\"<br/>" + "}")
	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public LoginResponse login(@RequestBody LoginRequest req,
			HttpServletResponse httpResponse) {
		String username = req.getUserId();
		String password = req.getPassword();
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
				username, password);
		LoginResponse result = new LoginResponse();
		try {
			Authentication auth = basicProvider.authenticate(authToken);
			// this will eventually delegate to our BasicAuthenticationProvider
			SecurityContextHolder.getContext().setAuthentication(auth);
			// return the authenticated user info
			User user = uSer.getUser(username);
			UserResponse resp = new UserResponse(user);
			result.setUser(resp);
			String token = generateRestToken(username);
			result.setSuccess(true);
			result.setApiToken(token);
			httpResponse.setHeader(RestConstants.API_KEY, token);
		} catch (Exception e) {
			result.setSuccess(false);
			result.setFailReason(e.getMessage());
		}
		return result;
	}
}
