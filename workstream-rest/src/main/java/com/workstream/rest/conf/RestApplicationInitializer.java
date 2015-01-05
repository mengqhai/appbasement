package com.workstream.rest.conf;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.workstream.rest.DispatcherServletConfiguration;
import com.workstream.rest.RestConstants;

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
		rootContext.setServletContext(servletContext);
		// see
		// http://stackoverflow.com/questions/25878554/spring-javaconfig-configuration-exception-a-servletcontext-is-required-to-conf
		rootContext.register(RestApplicationConfiguration.class);
		rootContext.refresh();
		servletContext.addListener(new ContextLoaderListener(rootContext));

		initSpring(servletContext, rootContext);
		// After this line:
		// The context hierarchy is like:
		// root -- RestApplicationConfiguration
		//  |_  child DispatcherServletConfiguration
		//
		
		// initSpringSecurity(servletContext);
		log.info("Web application fully configured");
	}

	/**
	 * Initialize Spring MVC.
	 * 
	 * @param servletContext
	 */
	private ServletRegistration.Dynamic initSpring(
			ServletContext servletContext,
			AnnotationConfigWebApplicationContext rootContext) {
		log.debug("Configuring Spring Web application context");
		AnnotationConfigWebApplicationContext dispatcherContext = new AnnotationConfigWebApplicationContext();
		dispatcherContext.setParent(rootContext);
		dispatcherContext.register(DispatcherServletConfiguration.class);

		log.debug("Registering Spring MVC Servlet");
		// see
		// http://www.rockhoppertech.com/blog/spring-mvc-configuration-without-xml/
		// http://www.oschina.net/question/214338_125228
		ServletRegistration.Dynamic dispatcherServlet = servletContext
				.addServlet("dispatcher", new DispatcherServlet(
						dispatcherContext));
		dispatcherServlet.addMapping(RestConstants.REST_ROOT + "/*");
		dispatcherServlet.setLoadOnStartup(1);
		dispatcherServlet.setAsyncSupported(true);
		log.debug("Spring MVC Servlet registered");
		return dispatcherServlet;
	}

	/**
	 * Initialize Spring security filter.
	 * 
	 * @param servletContext
	 */
	// private void initSpringSecurity(ServletContext servletContext) {
	// log.debug("Registering Spring Security Filter");
	// DelegatingFilterProxy proxy = new DelegatingFilterProxy();
	// FilterRegistration.Dynamic springSecurityFilter = servletContext
	// .addFilter("springSecurityFilterChain", proxy);
	// EnumSet<DispatcherType> disps = EnumSet.of(DispatcherType.REQUEST,
	// DispatcherType.FORWARD, DispatcherType.ASYNC);
	// springSecurityFilter.addMappingForUrlPatterns(disps, false, "/*");
	// springSecurityFilter.setAsyncSupported(true);
	// }

}
