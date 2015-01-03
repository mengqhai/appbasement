package com.workstream.rest.conf;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

/**
 * ' See http://docs.spring.io/autorepo/docs/spring/3.1.x/javadoc-api/org/
 * springframework/web/WebApplicationInitializer.html
 * 
 * @author qinghai
 * 
 */
public class RestApplicationInitializer implements WebApplicationInitializer {
	private static final Logger log = LoggerFactory
			.getLogger(WebApplicationInitializer.class);

	public void onStartup(ServletContext servletContext)
			throws ServletException {
		log.info("Configuring Spring root application context");
		AnnotationConfigWebApplicationContext rootContext;
		rootContext = new AnnotationConfigWebApplicationContext();
		rootContext.register(RestApplicationConfiguration.class);
		rootContext.refresh();
		servletContext.addListener(new ContextLoaderListener(rootContext));

		initSpring(servletContext);
		initSpringSecurity(servletContext);
		log.info("Web application fully configured");
	}

	/**
	 * Initialize Spring MVC.
	 * 
	 * @param servletContext
	 */
	private void initSpring(ServletContext servletContext) {

	}

	/**
	 * Initialize Spring security filter.
	 * 
	 * @param servletContext
	 */
	private void initSpringSecurity(ServletContext servletContext) {
		log.debug("Registering Spring Security Filter");
		FilterRegistration.Dynamic springSecurityFilter = servletContext
				.addFilter("springSecurityFilterChain",
						new DelegatingFilterProxy());
		EnumSet<DispatcherType> disps = EnumSet.of(DispatcherType.REQUEST,
				DispatcherType.FORWARD, DispatcherType.ASYNC);
		springSecurityFilter.addMappingForUrlPatterns(disps, false, "/*");
		springSecurityFilter.setAsyncSupported(true);
	}

}
