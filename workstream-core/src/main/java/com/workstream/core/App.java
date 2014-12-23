package com.workstream.core;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.workstream.core.conf.ApplicationConfiguration;
import com.workstream.core.service.OrganizationService;

/**
 * Hello world!
 * 
 */
public class App {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				ApplicationConfiguration.class);
		// RepositoryService repos = context.getBean(RepositoryService.class);
		// DeploymentQuery q = repos.createDeploymentQuery();
		// List<Deployment> deployList = q.list();
		// for (Deployment d : deployList) {
		// System.out.println(d.getName());
		// }

		OrganizationService service = context
				.getBean(OrganizationService.class);
		service.createOrg("hellowOrg", "helloOrg01",
				"Hello this a my first organization");

		context.close();
	}
}
