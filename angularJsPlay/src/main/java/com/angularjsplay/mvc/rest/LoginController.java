package com.angularjsplay.mvc.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.appbasement.persistence.IUserDAO;

@Controller
public class LoginController {

	@Autowired
	IUserDAO uDao;

	public LoginController() {
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
