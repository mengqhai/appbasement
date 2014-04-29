package com.angularjsplay.persistence;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.angularjsplay.model.Backlog;
import com.appbasement.persistence.GenericJpaDAO;

@Repository
public class BacklogJpaDAO extends GenericJpaDAO<Backlog, Long> implements
		IBacklogDAO {

	public BacklogJpaDAO() {
	}

	public BacklogJpaDAO(EntityManager em) {
		super(em);
	}

}
