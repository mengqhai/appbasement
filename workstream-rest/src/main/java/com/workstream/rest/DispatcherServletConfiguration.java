package com.workstream.rest;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
@ComponentScan({ "com.workstream.rest.controller" })
@EnableAsync
public class DispatcherServletConfiguration extends WebMvcConfigurationSupport {

}
