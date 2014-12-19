package sse.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

@WebListener
public class WebConfigurer implements ServletContextListener {

	public AnnotationConfigWebApplicationContext context;

	public void setContext(AnnotationConfigWebApplicationContext context) {
		this.context = context;
	}

	public void contextInitialized(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();
		AnnotationConfigWebApplicationContext rootContext = null;
		if (context == null) {
			rootContext = new AnnotationConfigWebApplicationContext();
			rootContext.register(ApplicationConfiguration.class);
			rootContext.refresh();
		} else {
			rootContext = context;
		}

		servletContext.setAttribute(
				WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
				rootContext);

	}

	public void contextDestroyed(ServletContextEvent sce) {
		WebApplicationContext ac = WebApplicationContextUtils
				.getRequiredWebApplicationContext(sce.getServletContext());
		AnnotationConfigWebApplicationContext gwac = (AnnotationConfigWebApplicationContext) ac;
		gwac.close();
	}

}
