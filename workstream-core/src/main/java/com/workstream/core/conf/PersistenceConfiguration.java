package com.workstream.core.conf;

import javax.sql.DataSource;

import org.hibernate.dialect.H2Dialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = { "com.workstream.core.persistence",
		"com.workstream.core.service" })
public class PersistenceConfiguration {
	// implements TransactionManagementConfigurer

	private final Logger log = LoggerFactory
			.getLogger(PersistenceConfiguration.class);

	@Autowired
	private Environment environment;

	@Autowired
	private DataSource dataSource; // reuse the dataSource of Activiti

	// https://jira.spring.io/browse/SPR-10787
//	@Override
//	public PlatformTransactionManager annotationDrivenTransactionManager() {
//		return txManager();
//	}

	@Bean
	@Scope("singleton")
	public PlatformTransactionManager wsTxManager() {
		JpaTransactionManager mgmt = new JpaTransactionManager();
		mgmt.setEntityManagerFactory(emf().getObject());
		log.info("JPA Transaction Manager created.");
		return mgmt;
	}

	@Bean
	public AbstractJpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter adpater = new HibernateJpaVendorAdapter();
		adpater.setDatabase(Database.H2);
		adpater.setShowSql(true);
		adpater.setGenerateDdl(true);
		adpater.setDatabasePlatform(H2Dialect.class.getName());
		log.info("Hibernate JPA Vendor Adapter created.");
		return adpater;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean emf() {
		LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
		bean.setDataSource(dataSource);
		bean.setPackagesToScan("com.workstream.core.model");
		bean.setJpaVendorAdapter(jpaVendorAdapter());
		log.info("EntityManager factory created.");
		return bean;
	}

}
