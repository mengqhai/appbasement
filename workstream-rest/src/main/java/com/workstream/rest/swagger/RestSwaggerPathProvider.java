package com.workstream.rest.swagger;

import javax.servlet.ServletContext;

import com.mangofactory.swagger.paths.RelativeSwaggerPathProvider;

public class RestSwaggerPathProvider extends RelativeSwaggerPathProvider {

	public RestSwaggerPathProvider(ServletContext servletContext) {
		super(servletContext);
		setApiResourcePrefix("rest");
	}

}
