package com.appbasement.mvc;

import java.util.Map;
import static com.appbasement.AppBasementConstants.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AppBasementHomeController {

	@RequestMapping({ APP_BASEMENT, APP_BASEMENT + "/home" })
	public String showHomePage(Map<String, Object> model) {
		return APP_BASEMENT + "/home";
	}

}
