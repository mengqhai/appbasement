package com.angularjsplay.persistence;

import javax.persistence.EntityManager;

import com.angularjsplay.model.Project;
import com.appbasement.persistence.GenericJpaDAO;

public class ProjectJpaDAO extends GenericJpaDAO<Project, Long> implements
		IProjectDAO {

	public ProjectJpaDAO() {
	}

	public ProjectJpaDAO(EntityManager em) {
		super(em);
	}

}
