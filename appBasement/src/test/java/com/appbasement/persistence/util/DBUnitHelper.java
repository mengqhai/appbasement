package com.appbasement.persistence.util;

import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.Session;

public class DBUnitHelper {

	public static IDataSet getFlatXmlDataSet(Class<?> clazz, String shortName) {
		String dataSetPath = "/" + clazz.getName().replaceAll("\\.", "/") + "_"
				+ shortName + ".xml";
		return getFlatXmlDataSet(dataSetPath);
	}

	public static IDataSet getFlatXmlDataSet(String dataSetPath) {
		try {
			InputStream iStream = DBUnitHelper.class
					.getResourceAsStream(dataSetPath);
			assertNotNull("Unable to find file:" + dataSetPath, iStream);
			Reader reader = new InputStreamReader(iStream);
			FlatXmlDataSet dataSet = new FlatXmlDataSet(reader);
			return dataSet;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(
					"Unable to load data set:" + dataSetPath, e);
		}
	}
	
	public static void cleanAll(EntityManagerFactory emf) {
		try {
			EntityManager em = emf.createEntityManager();
			final Session session = em.unwrap(Session.class);
			new TemplateWorker<Object>(em) {
				@Override
				protected void doIt() {
					// foreign key constraint, delete references
					// This is a must even with DatabaseSequenceFilter
					session.doWork(new DBUnitWork(DatabaseOperation.DELETE_ALL,
							TestConstants.DATA_SET_CLEAN_TABLES));
				}
			};
			em.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to clean data in DB.", e);
		}
	}

	public static void importDataSet(EntityManagerFactory emf,
			final String... dataSets) {
		try {
			EntityManager em = emf.createEntityManager();
			final Session session = em.unwrap(Session.class);
			new TemplateWorker<Object>(em) {
				@Override
				protected void doIt() {
					// import new data
					for (String dataSet : dataSets) {
						session.doWork(new DBUnitWork(
								DatabaseOperation.CLEAN_INSERT, dataSet));
					}
				}
			};
			em.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to import data set to DB.", e);
		}
	}

	public static void importBigDataSet(EntityManagerFactory emf) {
		importDataSet(emf, TestConstants.DATA_SET_USER,
				TestConstants.DATA_SET_GROUP,
				TestConstants.DATA_SET_TEMPLATE,
				TestConstants.DATA_SET_GROUP_USER);
	}

	public static void importSmallDataSet(EntityManagerFactory emf) {
		importDataSet(emf, TestConstants.DATA_SET_SMALL_USER,
				TestConstants.DATA_SET_SMALL_GROUP,
				TestConstants.DATA_SET_SMALL_TEMPLATE,
				TestConstants.DATA_SET_SMALL_GROUP_USER);

	}
}
