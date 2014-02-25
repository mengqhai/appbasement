package com.appbasement.mvc;

import static com.appbasement.AppBasementConstants.APP_BASEMENT;

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
import com.appbasement.service.user.IAppUserService;

@Controller
@RequestMapping("/" + APP_BASEMENT + "/group")
public class AppGroupController {

	@Autowired
	IAppUserService userService;

	public AppGroupController() {
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showGroupList(Map<String, Object> model) {
		List<Group> groups = userService.getAllGroups();
		model.put("groups", groups);
		return APP_BASEMENT + "/group";
	}

	@RequestMapping(method = RequestMethod.GET, params = "new")
	public String newGroup(Model model) {
		model.addAttribute(new Group());
		return APP_BASEMENT + "/group_edit";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String saveGroup(@Valid Group group, BindingResult bResult) {
		if (bResult.hasErrors()) {
			return APP_BASEMENT + "/group_edit";
		}
		if (group.getId() == null) {
			group.setCreatedAt(new Date());
		}
		userService.saveGroup(group);
		return "redirect:/" + APP_BASEMENT + "/group";
	}

	@RequestMapping(value = "/{groupId}", method = RequestMethod.GET)
	public String editGroup(@PathVariable Long groupId, Model model) {
		Group group = userService.getGroupById(groupId);
		model.addAttribute(group);
		return APP_BASEMENT + "/group_edit";
	}

}
