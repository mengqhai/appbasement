package com.angularjsplay.cors;

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
			String origin = request.getHeader("Origin");
			response.setHeader("Access-Control-Allow-Origin", origin == null ? "*"
					: origin);
			response.setHeader("Access-Control-Allow-Credentials", "true");
			// must do so, otherwise angularJs $http.get got error status code = 0
			return;
		} else {
			super.onLogoutSuccess(request, response, authentication);
		}
	}

}
