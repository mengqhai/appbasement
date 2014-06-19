package com.angularjsplay.cors;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;

public class CORS403ForbiddenEntryPoint extends Http403ForbiddenEntryPoint {

	public CORS403ForbiddenEntryPoint() {
	}

	@Override
	public void commence(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException arg2)
			throws IOException, ServletException {
		String origin = request.getHeader("Origin");
		response.setHeader("Access-Control-Allow-Origin", origin == null ? "*"
				: origin);
		response.setHeader("Access-Control-Allow-Credentials", "true");
		// must do so, otherwise angularJs $http.get got error status code = 0
		// e.g. error in Chrome: Credentials flag is 'true', but the
		// 'Access-Control-Allow-Credentials' header is ''. It must be 'true' to
		// allow credentials.

		// The W3 spec for CORS preflight requests clearly states that user
		// credentials should be excluded.
		// See
		// https://dvcs.w3.org/hg/cors/raw-file/tip/Overview.html#cross-origin-request-with-preflight-0
		// So we should allow a preflight request
		if (isPreflight(request)) {
			response.setHeader("Access-Control-Allow-Methods",
					"POST, GET, OPTIONS, DELETE, PUT, PATCH");

			response.setHeader("Access-Control-Max-Age", "3600");

			response.setHeader("Access-Control-Allow-Headers",
					"x-requested-with, origin, content-type, accept");
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		} else {
			super.commence(request, response, arg2);
		}
	}

	public boolean isPreflight(HttpServletRequest request) {
		return "OPTIONS".equals(request.getMethod());
	}

}
