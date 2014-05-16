package com.angularjsplay.mvc.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.angularjsplay.model.LoginRequest;
import com.angularjsplay.model.LoginResult;
import com.appbasement.persistence.IUserDAO;

@Controller
public class LoginController {

	@Autowired
	@Qualifier("org.springframework.security.authenticationManager")
	private AuthenticationManager authenticationManager;

	@Autowired
	IUserDAO uDao;

	public LoginController() {
	}

	/**
	 * Exposes a URL for REST API login, witch returns a User json as login
	 * success response.
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	@RequestMapping(value = "login", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	@ResponseBody
	public LoginResult restLogin(@Validated @RequestBody LoginRequest loginRequest) {
		String username = loginRequest.getUsername();
		String password = loginRequest.getPassword();
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
				username, password);
		LoginResult result = new LoginResult();
		try {
			Authentication auth = authenticationManager.authenticate(token);
			SecurityContextHolder.getContext().setAuthentication(auth);
			com.appbasement.model.User user = getUser();
			result.setSuccess(true);
			result.setUser(user);
		} catch (AuthenticationException e) {
			result.setSuccess(false);
			result.setFailReason(e.getMessage());
		}
		return result;
	}

	@RequestMapping(value = "currentUsername", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String getUsername() {
		User user = (User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		if (user == null) {
			return null;
		}
		String userName = user.getUsername();
		return userName;
	}

	@RequestMapping(value = "currentUser", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public com.appbasement.model.User getUser() {
		User user = (User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		if (user == null) {
			return null;
		}
		String userName = user.getUsername();
		com.appbasement.model.User userModel = uDao.findByUsername(userName);
		return userModel;
	}

}
