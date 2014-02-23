package com.appbasement.mvc;

import static com.appbasement.AppBasementConstants.APP_BASEMENT;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.appbasement.model.User;
import com.appbasement.service.user.IAppUserService;

@Controller
public class AppUserController {

	@Autowired
	IAppUserService userService;

	@RequestMapping({ APP_BASEMENT + "/user" })
	public void showUserList(Map<String, Object> model) {
		List<User> users = userService.getAllUsers();
		model.put("users", users);
	}

}
