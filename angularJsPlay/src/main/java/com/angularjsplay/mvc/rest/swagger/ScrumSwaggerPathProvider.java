package com.angularjsplay.mvc.rest.swagger;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.angularjsplay.mvc.rest.ScrumRestConstants;
import com.mangofactory.swagger.core.SwaggerPathProvider;

@Component
public class ScrumSwaggerPathProvider implements SwaggerPathProvider {
	@Autowired
	private ServletContext servletContext;
	private String apiResourcePreffix = ScrumRestConstants.REST_ROOT;
	private String baseUrl = ScrumRestConstants.BASE_URL;

	public ScrumSwaggerPathProvider() {
	}

	public ScrumSwaggerPathProvider(String baseUrl, String apiResourceSuffix) {
		this.apiResourcePreffix = apiResourceSuffix;
	}

	@Override
	public String getApiResourcePrefix() {
		return apiResourcePreffix;
	}

	@Override
	public String getAppBasePath() {
		return UriComponentsBuilder.fromHttpUrl(baseUrl)
				.path(servletContext.getContextPath()).path("/").build()
				.toString();
	}

	@Override
	public String sanitizeRequestMappingPattern(String requestMappingPattern) {
		String result = requestMappingPattern;
		// remove regex portion '/{businessId:\\w+}'
		result = result.replaceAll("\\{(.*?):.*?\\}", "{$1}");
		return result.isEmpty() ? "/" : result;
	}

}
