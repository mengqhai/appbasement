package com.workstream.core;

import java.util.List;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentQuery;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.workstream.core.conf.ApplicationConfiguration;

/**
 * Hello world!
 * 
 */
public class App {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				ApplicationConfiguration.class);
		RepositoryService repos = context.getBean(RepositoryService.class);
		DeploymentQuery q = repos.createDeploymentQuery();
		List<Deployment> deployList = q.list();
		for (Deployment d : deployList) {
			System.out.println(d.getName());
		}
		context.close();
	}
}
