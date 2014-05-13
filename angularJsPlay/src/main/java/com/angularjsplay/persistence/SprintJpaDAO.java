package com.angularjsplay.persistence;

import java.util.Collection;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.angularjsplay.model.Sprint;
import com.appbasement.persistence.GenericJpaDAO;

@Repository
public class SprintJpaDAO extends GenericJpaDAO<Sprint, Long> implements
		ISprintDAO {

	public SprintJpaDAO() {
	}

	public SprintJpaDAO(EntityManager em) {
		super(em);
	}

	@Override
	public Collection<Sprint> getSprintsForProject(Long projectId) {
		return getSprintsForProject(projectId, 0, Integer.MAX_VALUE);
	}

	@Override
	public Collection<Sprint> getSprintsForProject(Long projectId, int first,
			int max) {
		return filterFor("project.id", projectId, first, max);
	}

	@Override
	public Long getSprintCountForProject(Long projectId) {
		return countFilteredFor("project.id", projectId);
	}

}
