package com.angularjsplay.mvc.rest;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.angularjsplay.exception.ScrumResourceNotFoundException;
import com.angularjsplay.exception.ScrumValidationException;
import com.angularjsplay.mvc.validation.ValidateOnCreate;
import com.angularjsplay.mvc.validation.ValidateOnUpdate;
import com.appbasement.component.IObjectPatcher;
import com.appbasement.component.PatchedValue;
import com.appbasement.model.User;
import com.appbasement.service.user.IAppUserService;

@Controller
@RequestMapping(value = "/users", headers = "Accept=application/json")
public class UserController {

	@Autowired
	IAppUserService userService;

	@Autowired
	IObjectPatcher objectPatcher;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody
	List<User> listUsers() {
		return userService.getAllUsers();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public @ResponseBody
	User getUser(@PathVariable("id") long id) {
		User user = userService.getUserById(id);
		if (user == null) {
			throw new ScrumResourceNotFoundException();
		}
		return user;
	}

	@RequestMapping(method = { RequestMethod.PATCH, RequestMethod.PUT }, value = "/{id}")
	// @ResponseBody
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateUser(@PathVariable("id") long id,
			@RequestBody @Validated(value = ValidateOnUpdate.class) User user,
			BindingResult bResult) {
		if (bResult.hasErrors()) {
			throw new ScrumValidationException(bResult);
		} else {
			User existing = userService.getUserById(id);
			if (existing == null) {
				throw new ScrumResourceNotFoundException();
			}
			Map<Field, PatchedValue> patchedResult = objectPatcher.patchObject(
					existing, user);
			if (patchedResult.isEmpty()) {
				userService.saveUser(existing);
			}
			// return existing;
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public @ResponseBody
	User createUser(
			@RequestBody @Validated(value = ValidateOnCreate.class) User user,
			BindingResult bResult) {
		if (bResult.hasErrors()) {
			throw new ScrumValidationException(bResult);
		}
		userService.saveUser(user);
		return user;
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteUser(@PathVariable("id") long id) {
		userService.deleteUserById(id);
	}

}