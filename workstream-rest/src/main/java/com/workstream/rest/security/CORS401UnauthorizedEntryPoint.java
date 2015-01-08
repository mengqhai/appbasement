package com.workstream.rest.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * 
 * 401 Unauthorized, the HTTP status code for authentication errors. And that’s
 * just it: it’s for authentication, not authorization. Receiving a 401 response
 * is the server telling you, “you aren’t authenticated–either not authenticated
 * at all or authenticated incorrectly–but please reauthenticate and try again.”
 * To help you out, it will always include a WWW-Authenticate header that
 * describes how to authenticate.<br/>
 * So, for authorization I use the 403 Forbidden response. It’s permanent, it’s
 * tied to my application logic, and it’s a more concrete response than a 401.
 * 
 * Receiving a 403 response is the server telling you, “I’m sorry. I know who
 * you are–I believe who you say you are–but you just don’t have permission to
 * access this resource. Maybe if you ask the system administrator nicely,
 * you’ll get permission. But please don’t bother me again until your
 * predicament changes.”
 * 
 * In summary, a 401 Unauthorized response should be used for missing or bad
 * authentication, and a 403 Forbidden response should be used afterwards, when
 * the user is authenticated but isn’t authorized to perform the requested
 * operation on the given resource. see
 * http://stackoverflow.com/questions/3297048
 * /403-forbidden-vs-401-unauthorized-http-responses
 * 
 * @author qinghai
 * 
 */
public class CORS401UnauthorizedEntryPoint implements AuthenticationEntryPoint {

	public CORS401UnauthorizedEntryPoint() {
	}

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
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
					"You are not authenticated.");
		}
	}

	public boolean isPreflight(HttpServletRequest request) {
		return "OPTIONS".equals(request.getMethod());
	}

}
