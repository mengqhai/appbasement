package com.haoxin.emctest.conf;

import java.sql.Driver;

import javax.sql.DataSource;

import org.hibernate.dialect.H2Dialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * The spring configuration object for the persistence layer.
 *
 */
@Configuration
@EnableTransactionManagement
@PropertySources({ @PropertySource(value = "classpath:db.properties", ignoreResourceNotFound = true) })
public class PersistenceConfiguration {

	private final static Logger LOGGER = LoggerFactory.getLogger(PersistenceConfiguration.class);

	@Autowired
	private Environment environment;

	@Bean
	@Scope("singleton")
	public PlatformTransactionManager transactionManager() {
		JpaTransactionManager mgmt = new JpaTransactionManager();
		mgmt.setEntityManagerFactory(entityManagerFactory().getObject());
		return mgmt;
	}

	@Bean
	public DataSource dataSource() {
		SimpleDriverDataSource ds = new SimpleDriverDataSource();

		try {
			@SuppressWarnings("unchecked")
			Class<? extends Driver> driverClass = (Class<? extends Driver>) Class
					.forName(environment.getProperty("jdbc.driver", "org.h2.Driver"));
			ds.setDriverClass(driverClass);
		} catch (Exception e) {
			LOGGER.error("Failed to load driver class", e);
		}

		// Connection settings
		ds.setUrl(environment.getProperty("jdbc.url", "jdbc:h2:~/emctest/lunsimulator;DB_CLOSE_DELAY=1000"));
		ds.setUsername(environment.getProperty("jdbc.username", "sa"));
		ds.setPassword(environment.getProperty("jdbc.password", ""));
		return ds;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
		bean.setDataSource(dataSource());
		bean.setPackagesToScan("com.haoxin.emctest.model");
		bean.setJpaVendorAdapter(jpaVendorAdapter());
		LOGGER.info("EntityManager factory created.");
		return bean;
	}

	@Bean
	public AbstractJpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter adpater = new HibernateJpaVendorAdapter();
		adpater.setDatabase(Database.H2);
		adpater.setShowSql(true);
		adpater.setGenerateDdl(true);
		adpater.setDatabasePlatform(H2Dialect.class.getName());
		adpater.getJpaPropertyMap().put("hibernate.hbm2ddl.auto", "create-drop");
		adpater.getJpaPropertyMap().put("hibernate.format_sql", false);
		LOGGER.info("Hibernate JPA Vendor Adapter created.");
		return adpater;
	}

}
