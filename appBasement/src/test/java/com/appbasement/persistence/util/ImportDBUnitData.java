package com.appbasement.persistence.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class ImportDBUnitData {

	public static void main(String[] args) {
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("appBasementTest");
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		// Business Logic Start:
		DBUnitHelper.importDataSet(emf);
		// Business Logic End.
		tx.commit();
		em.close();
		emf.close();
	}

}
