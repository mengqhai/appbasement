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
		// e.g. error in Chrome: Credentials flag is 'true', but the 'Access-Control-Allow-Credentials' header is ''. It must be 'true' to allow credentials. 
		super.commence(request, response, arg2);
	}
	
	

}
