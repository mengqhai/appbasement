package com.appbasement.mvc;

import static com.appbasement.AppBasementConstants.APP_BASEMENT;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.appbasement.model.Group;
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
	public String saveUser(@Valid User user, BindingResult bResult) {
		if (bResult.hasErrors()) {
			return APP_BASEMENT + "/user_edit";
		}

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

	@RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
	public String deleteUser(@PathVariable Long userId) {
		userService.deleteUserById(userId);
		return "redirect:/" + APP_BASEMENT + "/user";
	}

	@RequestMapping(value = "/{userId}/group", method = RequestMethod.GET)
	public String showUserGroups(@PathVariable Long userId, Model model) {
		User user = userService.getUserWithEagerGroups(userId);
		model.addAttribute(user);

		// for group adding:

		// only put available groups for selection
		List<Group> avaGroups = userService.getAllGroups();
		avaGroups.removeAll(user.getGroups());
		model.addAttribute("avaGroups", avaGroups);
		AddUserToGroup addUserToGroup = new AddUserToGroup();
		addUserToGroup.setAddTo(new ArrayList<Long>());
		model.addAttribute(addUserToGroup);
		return APP_BASEMENT + "/user_group";
	}

	@RequestMapping(value = "/{userId}/group/{groupId}", method = RequestMethod.DELETE)
	public String removeUserFromGroup(@PathVariable Long userId,
			@PathVariable Long groupId) {
		userService.removeUserFromGroup(userId, groupId);
		return "redirect:/" + APP_BASEMENT + "/user/" + userId + "/group";
	}

	@RequestMapping(value = "/{userId}/group", method = RequestMethod.PUT)
	public String addUserToGroups(@PathVariable Long userId,
			AddUserToGroup addUserToGroup) {
		List<Long> addTo = addUserToGroup.getAddTo();
		System.out.println(addUserToGroup.getAddTo());
		userService.addUserToGroup(userId, addTo.toArray(new Long[] {}));
		return "redirect:/" + APP_BASEMENT + "/user/" + userId + "/group";
	}

}

/**
 * A form model object to hold the selected group ids.
 * 
 * 
 * @author qinghai
 * 
 */
class AddUserToGroup {
	private List<Long> addTo;

	public List<Long> getAddTo() {
		return addTo;
	}

	public void setAddTo(List<Long> addTo) {
		this.addTo = addTo;
	}
}
