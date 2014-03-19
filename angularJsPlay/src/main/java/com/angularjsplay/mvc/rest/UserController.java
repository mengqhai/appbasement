package com.angularjsplay.mvc.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.appbasement.model.User;
import com.appbasement.service.user.IAppUserService;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	IAppUserService userService;

	@RequestMapping(method = RequestMethod.GET, headers = "Accept=application/json")
	public @ResponseBody List<User> listUsers() {
		return userService.getAllUsers();
	}

}
