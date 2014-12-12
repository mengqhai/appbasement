package com.workstream.core.conf;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({
		@PropertySource(value = "classpath:db.properties", ignoreResourceNotFound = true),
		@PropertySource(value = "classpath:engine.properties", ignoreResourceNotFound = true) })
@ComponentScan(basePackages = "com.workstream.core.conf")
public class ApplicationConfiguration {

}
