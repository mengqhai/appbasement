package com.workstream.rest.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * This is how we currently do it in the webapp: when a new request comes in, we
 * set the authenticated user. When all is done, and the response is sent back,
 * we set null. See
 * http://forums.activiti.org/content/historicprocessinstancegetstartuserid
 * -return-null
 * 
 * @author qinghai
 * 
 */
@WebFilter(filterName = "activitiAuthentication", urlPatterns = { "/*" })
public class ActivitiAuthenticationSettingFilter implements Filter {

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		if (auth != null && !(auth instanceof AnonymousAuthenticationToken)) {
			org.activiti.engine.impl.identity.Authentication
					.setAuthenticatedUserId(auth.getName());
		}

		chain.doFilter(request, response);

		org.activiti.engine.impl.identity.Authentication
				.setAuthenticatedUserId(null);
	}

	public void destroy() {
	}

}
