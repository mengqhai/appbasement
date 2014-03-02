package com.appbasement.mvc.spa;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static com.appbasement.AppBasementConstants.APP_BASEMENT;

/**
 * The controller for user management Single-page Application (SPA)
 * 
 * @author liuli
 *
 */
@Controller
@RequestMapping("/"+APP_BASEMENT+"/spa")
public class AppUserSpaController {
	
	
	@RequestMapping(method=RequestMethod.GET)
	public String bootstrapApp() {
		return APP_BASEMENT+"/spa/index";
	}

}
