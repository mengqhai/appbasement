package com.workstream.rest.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter(filterName = "corsFilter", urlPatterns = { "/*" })
public class SimpleCORSFilter implements Filter {

	public SimpleCORSFilter() {
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;

		HttpServletResponse response = (HttpServletResponse) res;

		String origin = request.getHeader("Origin");
		response.setHeader("Access-Control-Allow-Origin", origin == null ? "*"
				: origin);

		response.setHeader("Access-Control-Allow-Credentials", "true");

		response.setHeader("Access-Control-Allow-Methods",
				"POST, GET, OPTIONS, DELETE, PUT, PATCH");

		response.setHeader("Access-Control-Max-Age", "3600");

		response.setHeader("Access-Control-Allow-Headers",
				"x-requested-with, origin, content-type, accept");

		chain.doFilter(req, res);

	}

	public void init(FilterConfig arg0) throws ServletException {
	}

}
