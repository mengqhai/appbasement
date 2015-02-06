package com.workstream.rest.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.util.DigestUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.workstream.core.service.UserService;
import com.workstream.rest.RestConstants;
import com.workstream.rest.exception.BeanValidationException;
import com.workstream.rest.model.LoginRequest;
import com.workstream.rest.model.LoginResponse;
import com.workstream.rest.model.SingleValueResponse;
import com.workstream.rest.model.UserResponse;
import com.workstream.rest.security.RestTokenSecurityContextRepository;
import com.workstream.rest.utils.RestUtils;

@Api(value = "login", description = "Login endpoint", position = 1)
@RestController
@RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestLoginController {

	private static final Logger log = LoggerFactory
			.getLogger(RestLoginController.class);

	@Autowired
	private RestTokenSecurityContextRepository securityCtxRepo;

	/**
	 * If expose the AuthenticationManager from WebSecurityConfigurerAdapter,
	 * authentication will somehow encounter the dead loop problem. So here I
	 * have to wire my own basic provider.
	 */
	@Autowired
	private AuthenticationProvider basicProvider;

	@Autowired
	private UserService uSer;

	@Autowired
	private SessionRegistry sReg;

	protected String generateRestToken(String authName) {
		String org = authName + System.currentTimeMillis();
		String token = DigestUtils.md5DigestAsHex(org.getBytes());
		return token;
	}

	@ApiOperation(value = "A login operation, this operation will generate an api_token that the client can further use.", notes = "Try login the test user: <br/><br/>"
			+ "<b>{ <br/>"
			+ "\"password\": \"passw0rd\",<br/>"
			+ "\"userId\": \"mqhnow1@sina.com\"<br/>" + "}</b>")
	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public LoginResponse login(
			@ApiParam(required = true) @RequestBody @Validated LoginRequest req,
			BindingResult bResult, @ApiIgnore HttpServletResponse httpResponse,
			@ApiIgnore HttpServletRequest httpRequest) {
		if (bResult.hasErrors()) {
			throw new BeanValidationException(bResult);
		}

		String username = req.getUserId();
		String password = req.getPassword();
		LoginResponse result = new LoginResponse();
		try {

			// Legacy code, replaced by request.login()
			// UsernamePasswordAuthenticationToken authToken = new
			// UsernamePasswordAuthenticationToken(
			// username, password);
			// Authentication auth = basicProvider.authenticate(authToken);
			// this will eventually delegate to our BasicAuthenticationProvider
			// SecurityContextHolder.getContext().setAuthentication(auth);
			// return the authenticated user info

			// Servlet 3.0 API;
			httpRequest.login(username, password);
			String newSessionId = httpRequest.getSession().getId();

			// when not using the BasicAuthenticationFilter to do the auth,
			// the SessionManagementFilter won't call the
			// sessionAuthenticationStrategy.onAuthentication().
			// This causes the sessionRegistry's registerNewSession() was never
			// called(by RegisterSessionAuthenticationStrategy or
			// ConcurrentSessionControlStrategy).
			// see SessionManagementFilter.doFilter()
			// So here we have to explicitly call the registerNewSession().
			sReg.registerNewSession(newSessionId, username);

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

	@ApiOperation(value = "Count current login number", notes = RestConstants.TEST_USER_ID_INFO)
	@RequestMapping(value = "/login/_count", method = RequestMethod.GET)
	@PreAuthorize("principal == decodeUserId(#userIdBase64)")
	public SingleValueResponse getLoggedInUserCount(
			@RequestParam(value = "userId", required = false) String userIdBase64) {
		int count = 0;
		if (userIdBase64 == null || userIdBase64.isEmpty()) {
			count = securityCtxRepo.getSecurityContextCount();
		} else {
			String userId = RestUtils.decodeUserId(userIdBase64);
			count = securityCtxRepo.getSecurityContextByUser(userId).size();
		}
		return new SingleValueResponse(count);
	}

	@ApiOperation(value = "[Test only]Kick a user out", notes = RestConstants.TEST_USER_ID_INFO)
	@RequestMapping(value = "/login/kick", method = RequestMethod.PUT)
	public void kickOutUser(
			@RequestParam(value = "userId", required = true) String userIdBase64) {
		String userId = RestUtils.decodeUserId(userIdBase64);
		kickOutUserById(userId);
	}

	int kickOutUserById(String userId) {
		if (sReg.getAllPrincipals().contains(userId)) {
			List<SessionInformation> sessions = sReg.getAllSessions(userId,
					false);
			for (SessionInformation s : sessions) {
				s.expireNow(); // force the session to expire
			}
		}
		int clearedCount = securityCtxRepo.clearSecurityContextByUser(userId);
		log.info("Kicked {} logins for user {}", clearedCount, userId);
		return clearedCount;
	}

	void addGroupsToAuthentication(String userId, List<Group> groups) {
		for (Group group : groups) {
			securityCtxRepo.addRoleToAuthentication(userId, group.getId());
		}
	}

	void addGroupsToAuthentication(String userId, String... groupIds) {
		for (String groupId : groupIds) {
			securityCtxRepo.addRoleToAuthentication(userId, groupId);
		}
	}

	@ApiOperation(value = "Logout the current user")
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public void logout(@ApiIgnore HttpServletRequest request,
			@ApiIgnore HttpServletResponse response) {
		securityCtxRepo.clearSecurityContextByToken(request);
		try {
			response.sendRedirect(request.getContextPath() + "/logout");
		} catch (IOException e) {
			log.error("Failed to send logout redirect", e);
		}
	}
}
