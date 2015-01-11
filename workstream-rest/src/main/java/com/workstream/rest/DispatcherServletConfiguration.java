package com.workstream.rest;

import java.util.List;

import org.activiti.workflow.simple.definition.ChoiceStepsDefinition;
import org.activiti.workflow.simple.definition.DelayStepDefinition;
import org.activiti.workflow.simple.definition.FeedbackStepDefinition;
import org.activiti.workflow.simple.definition.HumanStepDefinition;
import org.activiti.workflow.simple.definition.ListConditionStepDefinition;
import org.activiti.workflow.simple.definition.ListStepDefinition;
import org.activiti.workflow.simple.definition.ParallelStepsDefinition;
import org.activiti.workflow.simple.definition.ScriptStepDefinition;
import org.activiti.workflow.simple.definition.form.BooleanPropertyDefinition;
import org.activiti.workflow.simple.definition.form.DatePropertyDefinition;
import org.activiti.workflow.simple.definition.form.ListPropertyDefinition;
import org.activiti.workflow.simple.definition.form.NumberPropertyDefinition;
import org.activiti.workflow.simple.definition.form.ReferencePropertyDefinition;
import org.activiti.workflow.simple.definition.form.TextPropertyDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Configuration for the MVC dispatcher servlet context which is separate from
 * the root context.
 * 
 * @author qinghai
 * 
 */
@Configuration
@ComponentScan({ "com.workstream.rest.controller",
		"com.workstream.rest.swagger" })
@EnableAsync
// @EnableWebMvc (use it if the configuration is extending
// WebMvcConfigurerAdapter, DO NOT use this annotation if your configuration
// bean extends
// WebMvcConfigurationSupport, otherwise overriden methods won't be invoked)
public class DispatcherServletConfiguration extends WebMvcConfigurationSupport {

	@Bean
	public MultipartResolver multipartResolver() {
		// for upload file support
		CommonsMultipartResolver resolver = new CommonsMultipartResolver();
		resolver.setMaxUploadSize(2048 * 1024L); // 2MB
		return resolver;
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		// see SimpleWorkflowJsonConvertor.getObjectMapper()
		// and
		// http://stackoverflow.com/questions/19239413/de-serializing-json-to-polymorphic-object-model-using-spring-and-jsontypeinfo-an

		// Register all property-definition model classes as sub-types, otherwise
		// jackson won't understand the subtype json when deserializing
		mapper.registerSubtypes(ListPropertyDefinition.class,
				TextPropertyDefinition.class,
				ReferencePropertyDefinition.class,
				DatePropertyDefinition.class, NumberPropertyDefinition.class,
				BooleanPropertyDefinition.class);

		// Register all step-types
		mapper.registerSubtypes(HumanStepDefinition.class,
				FeedbackStepDefinition.class, ParallelStepsDefinition.class,
				ChoiceStepsDefinition.class, ListStepDefinition.class,
				ListConditionStepDefinition.class, ScriptStepDefinition.class,
				DelayStepDefinition.class);
		return mapper;
	}

	@Override
	public void configureMessageConverters(
			List<HttpMessageConverter<?>> converters) {
		this.addDefaultHttpMessageConverters(converters);
		for (HttpMessageConverter<?> con : converters) {
			if (con instanceof MappingJackson2HttpMessageConverter) {
				MappingJackson2HttpMessageConverter jackson = (MappingJackson2HttpMessageConverter) con;
				jackson.setObjectMapper(objectMapper());
			}
		}
	}

}
