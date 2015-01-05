package com.workstream.rest;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.mangofactory.swagger.plugin.EnableSwagger;

/**
 * Configuration for the MVC dispatcher servlet context which is separate from
 * the root context.
 * 
 * @author qinghai
 * 
 */
@Configuration
@ComponentScan({ "com.workstream.rest.controller" })
@EnableAsync
@EnableSwagger
// This has to be done in the same spring context as the dispatcher-servlet
// context, otherwise the swagger request mappings are separated from the
// dispatcher's, and become useless.
@EnableWebMvc
public class DispatcherServletConfiguration extends WebMvcConfigurationSupport {

}
