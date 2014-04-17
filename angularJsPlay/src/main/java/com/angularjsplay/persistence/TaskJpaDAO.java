package com.angularjsplay.persistence;

import javax.persistence.EntityManager;

import com.angularjsplay.model.Task;
import com.appbasement.persistence.GenericJpaDAO;

public class TaskJpaDAO extends GenericJpaDAO<Task, Long> implements ITaskDAO {

	public TaskJpaDAO() {
	}

	public TaskJpaDAO(EntityManager em) {
		super(em);
	}

}
