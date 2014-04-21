package com.angularjsplay.mvc.rest;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.angularjsplay.mvc.validation.ValidateOnCreate;
import com.angularjsplay.mvc.validation.ValidateOnUpdate;
import com.appbasement.model.User;
import com.appbasement.service.user.IAppUserService;

@Controller
@RequestMapping(value = "/user", headers = "Accept=application/json")
public class UserController {

	@Autowired
	IAppUserService userService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody
	List<User> listUsers() {
		return userService.getAllUsers();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public @ResponseBody
	User getUser(@PathVariable("id") long id) {
		return userService.getUserById(id);
	}

	@RequestMapping(method = RequestMethod.PATCH, value = "/{id}")
	public void updateUser(@PathVariable("id") long id,
			@RequestBody @Validated(value = ValidateOnCreate.class) User user,
			BindingResult bResult) {
		userService.saveUser(user);
	}

}