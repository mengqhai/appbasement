package com.workstream.rest.swagger;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.paths.SwaggerPathProvider;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;

@EnableSwagger
// This has to be done in the same spring context as the dispatcher-servlet
// context, otherwise the swagger request mappings are separated from the
// dispatcher's, and become useless.
@Configuration
public class SwaggerConfiguration {

	@Autowired
	private ServletContext servletContext;

	@Autowired
	private SpringSwaggerConfig springSwaggerConfig;

	@Bean
	public SwaggerPathProvider swaggerPathProvider() {
		return new RestSwaggerPathProvider(servletContext);
	}

	@Bean
	// Don't forget the @Bean annotation
	public SwaggerSpringMvcPlugin customImplementation() {
		return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
				.pathProvider(swaggerPathProvider()).apiInfo(
						new ApiInfo("Work Stream REST API", "", "",
								"mengqhai@gmail.com", "", ""));
	}
}
