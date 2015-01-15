package com.workstream.core.conf;

import java.sql.Driver;

import javax.sql.DataSource;

import org.hibernate.dialect.H2Dialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

import com.workstream.core.model.BinaryObj.BinaryReposType;
import com.workstream.core.persistence.binary.BinaryRepositoryManager;
import com.workstream.core.persistence.binary.FileSystemBinaryRepository;

@Configuration
@EnableTransactionManagement
public class PersistenceConfiguration {
	// implements TransactionManagementConfigurer

	private final Logger log = LoggerFactory
			.getLogger(PersistenceConfiguration.class);

	@Autowired
	private Environment environment;

	@Autowired
	private DataSource dataSource; // reuse the dataSource of Activiti

	// https://jira.spring.io/browse/SPR-10787
	// @Override
	// public PlatformTransactionManager annotationDrivenTransactionManager() {
	// return txManager();
	// }

	@Bean
	@Scope("singleton")
	public PlatformTransactionManager wsTxManager() {
		JpaTransactionManager mgmt = new JpaTransactionManager();
		mgmt.setEntityManagerFactory(emf().getObject());
		log.info("JPA Transaction Manager created.");
		return mgmt;
	}

	@Bean
	public DataSource dataSource() {
		SimpleDriverDataSource ds = new SimpleDriverDataSource();

		try {
			@SuppressWarnings("unchecked")
			Class<? extends Driver> driverClass = (Class<? extends Driver>) Class
					.forName(environment.getProperty("jdbc.driver",
							"org.h2.Driver"));
			ds.setDriverClass(driverClass);
		} catch (Exception e) {
			log.error("Failed to load driver class", e);
		}

		// Connection settings
		ds.setUrl(environment.getProperty("jdbc.url",
				"jdbc:h2:mem:activiti;DB_CLOSE_DELAY=1000"));
		ds.setUsername(environment.getProperty("jdbc.username", "sa"));
		ds.setPassword(environment.getProperty("jdbc.password", ""));
		return ds;
	}

	@Bean
	public AbstractJpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter adpater = new HibernateJpaVendorAdapter();
		adpater.setDatabase(Database.H2);
		adpater.setShowSql(true);
		// adpater.setGenerateDdl(true);
		adpater.setDatabasePlatform(H2Dialect.class.getName());
		adpater.getJpaPropertyMap()
				.put("hibernate.hbm2ddl.auto", "create-drop");
		adpater.getJpaPropertyMap().put("hibernate.format_sql", false);
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

	@Bean
	public BinaryRepositoryManager binaryRepositoryManager() {
		BinaryRepositoryManager mgr = new BinaryRepositoryManager();
		FileSystemBinaryRepository fileRepo = new FileSystemBinaryRepository();
		fileRepo.setRepositoryRootPath("E:/workspaces/workspace_activiti/BinaryRepository");
		mgr.addRepository(BinaryReposType.FILE_SYSTEM_REPOSITORY, fileRepo);
		return mgr;
	}

}
