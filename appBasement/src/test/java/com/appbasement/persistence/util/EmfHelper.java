package com.appbasement.persistence.util;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EmfHelper {

	private static EntityManagerFactory emf = null;

	public static void initEmf() {
		emf = Persistence.createEntityManagerFactory(TestConstants.PERSISTENCE_UNIT);
	}

	public static void closeEmf() {
		if (emf != null) {
			emf.close();
		}
	}

	public static EntityManagerFactory getEmf() {
		return emf;
	}

}
