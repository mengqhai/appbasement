package com.angularjsplay.persistence;

import javax.persistence.EntityManager;

import com.angularjsplay.model.Backlog;
import com.appbasement.persistence.GenericJpaDAO;

public class BacklogJpaDAO extends GenericJpaDAO<Backlog, Long> implements
		IBacklogDAO {

	public BacklogJpaDAO() {
	}

	public BacklogJpaDAO(EntityManager em) {
		super(em);
	}

}
