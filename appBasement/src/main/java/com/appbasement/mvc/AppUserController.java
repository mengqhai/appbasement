package com.appbasement.mvc;

import static com.appbasement.AppBasementConstants.APP_BASEMENT;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.appbasement.model.User;
import com.appbasement.service.user.IAppUserService;

@Controller
@RequestMapping("/" + APP_BASEMENT + "/user")
public class AppUserController {

	@Autowired
	IAppUserService userService;

	@RequestMapping(method = RequestMethod.GET)
	public void showUserList(Map<String, Object> model) {
		List<User> users = userService.getAllUsers();
		model.put("users", users);
	}

	@RequestMapping(method = RequestMethod.POST)
	public String saveUser(User user) {
		if (user.getId() == null) {
			// fix creation time
			user.setCreatedAt(new Date());
		}
		userService.saveUser(user);
		return "redirect:/" + APP_BASEMENT + "/user";
	}

	@RequestMapping(method = RequestMethod.GET, params = "new")
	public String newUser(Model model) {
		model.addAttribute(new User());
		return APP_BASEMENT + "/user_edit";
	}

	@RequestMapping(value = "/{userId}", method = RequestMethod.GET)
	public String editUser(@PathVariable Long userId, Model model) {
		User user = userService.getUserById(userId);
		model.addAttribute(user);
		return APP_BASEMENT + "/user_edit";
	}

}
