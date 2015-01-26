package com.workstream.rest.conf;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * The configuration for application root context.
 * 
 * 
 * See: http://stackoverflow.com/questions/17898606/difference-between-
 * webmvcconfigurationsupport-and-webmvcconfigureradapter
 * 
 * 
 * @author qinghai
 * 
 */
@Configuration
@PropertySources({
		@PropertySource(value = "classpath:db.properties", ignoreResourceNotFound = true),
		@PropertySource(value = "classpath:engine.properties", ignoreResourceNotFound = true) })
@ComponentScan(basePackages = { "com.workstream.rest.conf",
		"com.workstream.core.conf", "com.workstream.rest.exception" })
public class RestApplicationConfiguration extends WebMvcConfigurerAdapter {

}
