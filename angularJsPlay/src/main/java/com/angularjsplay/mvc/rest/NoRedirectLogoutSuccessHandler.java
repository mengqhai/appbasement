package com.angularjsplay.mvc.rest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

public class NoRedirectLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

	public NoRedirectLogoutSuccessHandler() {
	}

	@Override
	public void onLogoutSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		String noRedirect = request.getParameter("noRedirect");
		if (noRedirect != null) {
			response.setHeader("Access-Control-Allow-Origin", "*");
			// must do so, otherwise angularJs $http.get got error status code = 0
			return;
		} else {
			super.onLogoutSuccess(request, response, authentication);
		}
	}

}
