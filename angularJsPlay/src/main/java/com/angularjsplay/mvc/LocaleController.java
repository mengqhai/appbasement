package com.angularjsplay.mvc;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/locale")
@Controller
public class LocaleController {

	public LocaleController() {
	}

	@RequestMapping("/angular-locale.js")
	public String redirectAngularJsLocale(HttpServletRequest request) {
		Locale lo = request.getLocale();
		
		String lang = lo.toLanguageTag().toLowerCase();
		return "redirect:/resources/angular-i18n/angular-locale_" + lang + ".js";
	}

}
