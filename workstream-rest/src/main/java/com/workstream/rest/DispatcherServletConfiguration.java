package com.workstream.rest;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

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
@EnableWebMvc
public class DispatcherServletConfiguration extends WebMvcConfigurationSupport {

}
