package com.workstream.core.conf;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@PropertySources({
		@PropertySource(value = "classpath:db.properties", ignoreResourceNotFound = true),
		@PropertySource(value = "classpath:engine.properties", ignoreResourceNotFound = true) })
@ComponentScan(basePackages = { "com.workstream.core.conf",
		"com.workstream.core.persistence", "com.workstream.core.service" })
@EnableAsync
// required for CoreEventService.notifySubscribers()
public class ApplicationConfiguration {

}
