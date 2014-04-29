package com.angularjsplay.persistence;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.angularjsplay.model.Task;
import com.appbasement.persistence.GenericJpaDAO;

@Repository
public class TaskJpaDAO extends GenericJpaDAO<Task, Long> implements ITaskDAO {

	public TaskJpaDAO() {
	}

	public TaskJpaDAO(EntityManager em) {
		super(em);
	}

}
