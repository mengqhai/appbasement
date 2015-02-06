package com.workstream.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.workstream.core.service.components.WsObjectMapper;
import com.workstream.rest.security.exp.WsSecurityExpressionHandler;

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

	@Autowired
	private WsObjectMapper objectMapper;

	@Bean
	public MultipartResolver multipartResolver() {
		// for upload file support
		CommonsMultipartResolver resolver = new CommonsMultipartResolver();
		// resolver.setMaxUploadSize(2048 * 1024L); // 2MB
		return resolver;
	}

	@Override
	public void configureMessageConverters(
			List<HttpMessageConverter<?>> converters) {
		this.addDefaultHttpMessageConverters(converters);
		for (HttpMessageConverter<?> con : converters) {
			if (con instanceof MappingJackson2HttpMessageConverter) {
				MappingJackson2HttpMessageConverter jackson = (MappingJackson2HttpMessageConverter) con;
				jackson.setObjectMapper(objectMapper);
			}
		}
	}

	@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
	// http://docs.spring.io/spring-security/site/faq/faq.html#faq-method-security-in-web-context
	public static class WsGlablMethodSecurityConfiguration extends
			GlobalMethodSecurityConfiguration {

		@Override
		protected MethodSecurityExpressionHandler createExpressionHandler() {
			return wsSecurityExpressionHandler();
		}

		@Bean
		public WsSecurityExpressionHandler wsSecurityExpressionHandler() {
			return new WsSecurityExpressionHandler();
		}
	}

}
