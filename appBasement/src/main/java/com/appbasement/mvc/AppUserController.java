package com.appbasement.mvc;

import static com.appbasement.AppBasementConstants.APP_BASEMENT;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.appbasement.model.User;
import com.appbasement.service.user.IAppUserService;

@Controller
public class AppUserController {

	@Autowired
	IAppUserService userService;

	@RequestMapping(value = { APP_BASEMENT + "/user" }, method = RequestMethod.GET)
	public void showUserList(Map<String, Object> model) {
		List<User> users = userService.getAllUsers();
		model.put("users", users);
	}

	@RequestMapping(value = { APP_BASEMENT + "/user" }, method = RequestMethod.GET, params = "new")
	public String newUser(Model model) {
		model.addAttribute(new User());
		return APP_BASEMENT + "/user_edit";
	}

	public void editUser(Model model) {

	}

	@RequestMapping(value = { APP_BASEMENT + "/user" }, method = RequestMethod.POST)
	public void saveUser(Map<String, Object> model) {

	}

}
