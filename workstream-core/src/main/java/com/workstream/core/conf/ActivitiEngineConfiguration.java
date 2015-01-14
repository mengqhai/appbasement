package com.workstream.core.conf;

import javax.sql.DataSource;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

import com.workstream.core.event.CoreProcessEventListener;
import com.workstream.core.event.CoreTaskCommentEventListener;
import com.workstream.core.event.CoreTaskEventListener;
import com.workstream.core.exception.ConfigurationException;

@Configuration
public class ActivitiEngineConfiguration {

	private final Logger log = LoggerFactory
			.getLogger(ActivitiEngineConfiguration.class);

	@Autowired
	private Environment environment;

	@Autowired
	private PlatformTransactionManager wsTxManager;

	@Autowired
	private DataSource dataSource;

	// @Bean
	// public PlatformTransactionManager actTransactionManager() {
	// DataSourceTransactionManager transactionManager = new
	// DataSourceTransactionManager();
	// transactionManager.setDataSource(dataSource());
	// return transactionManager;
	// }

	@Bean
	public ProcessEngineConfigurationImpl processEngineConfiguration() {
		SpringProcessEngineConfiguration cfg = new SpringProcessEngineConfiguration();
		cfg.setDataSource(dataSource);
		cfg.setDatabaseSchemaUpdate(environment.getProperty(
				"engine.schema.update", "true"));
		cfg.setTransactionManager(wsTxManager);
		cfg.setJobExecutorActivate(Boolean.valueOf(environment.getProperty(
				"engine.activate.jobexecutor", "false")));
		cfg.setHistory(environment.getProperty("engine.history.level", "full"));

		// auto deploy system leave templates
		cfg.setDeploymentResources(new Resource[] { new ClassPathResource(
				"com/workstream/core/sysprocess/UserJoinOrg.bpmn") });
		cfg.setActivityFontName("sansserif");
		cfg.setLabelFontName("sansserif"); // to support Chinese in diagram
		return cfg;
	}

	@Bean
	public ProcessDiagramGenerator diagramGenerator() {
		return processEngineConfiguration().getProcessDiagramGenerator();
	}

	@Bean
	public ProcessEngineFactoryBean processEngineFactoryBean() {
		ProcessEngineFactoryBean factoryBean = new ProcessEngineFactoryBean();
		factoryBean.setProcessEngineConfiguration(processEngineConfiguration());
		return factoryBean;
	}

	@Bean
	public ProcessEngine processEngine() {
		// Safe to call the getObject() on the @Bean annotated
		// processEngineFactoryBean(), will be
		// the fully initialized object instanced from the factory and will NOT
		// be created more than once
		try {
			return processEngineFactoryBean().getObject();
		} catch (Exception e) {
			log.error("Failed create process engine.", e);
			throw new ConfigurationException(e);
		}
	}

	@Bean
	public RepositoryService repositoryService() {
		return processEngine().getRepositoryService();
	}

	@Bean
	public CoreTaskEventListener taskEventListener() {
		return new CoreTaskEventListener();
	}

	@Bean
	public CoreTaskCommentEventListener commentEventListener() {
		return new CoreTaskCommentEventListener();
	}

	@Bean
	public CoreProcessEventListener processEventListener() {
		return new CoreProcessEventListener();
	}

	@Bean
	public RuntimeService runtimeService() {
		RuntimeService ru = processEngine().getRuntimeService();
		ru.addEventListener(taskEventListener(),
				CoreTaskEventListener.EVENT_TYPES);
		ru.addEventListener(commentEventListener(),
				CoreTaskCommentEventListener.EVENT_TYPES);
		ru.addEventListener(processEventListener(),
				CoreProcessEventListener.EVENT_TYPES);
		return ru;
	}

	@Bean
	public TaskService taskService() {
		return processEngine().getTaskService();
	}

	@Bean
	public HistoryService historyService() {
		return processEngine().getHistoryService();
	}

	@Bean
	public FormService formService() {
		return processEngine().getFormService();
	}

	@Bean
	public IdentityService identityService() {
		return processEngine().getIdentityService();
	}

	@Bean
	public ManagementService managementService() {
		return processEngine().getManagementService();
	}

}
