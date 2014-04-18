package com.angularjsplay.persistence;

import javax.persistence.EntityManager;

import com.angularjsplay.model.Sprint;
import com.appbasement.persistence.GenericJpaDAO;

public class SprintJpaDAO extends GenericJpaDAO<Sprint, Long> implements
		ISprintDAO {

	public SprintJpaDAO() {
	}

	public SprintJpaDAO(EntityManager em) {
		super(em);
	}

}
