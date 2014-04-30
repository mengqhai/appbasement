package com.angularjsplay.persistence;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

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
		TypedQuery<Sprint> q = getEm().createQuery(
				"select s from Sprint as s where s.project.id=:projectId",
				Sprint.class);
		q.setParameter("projectId", projectId);
		q.setFirstResult(first);
		q.setMaxResults(max);
		return q.getResultList();
	}

	@Override
	public Long getSprintCountForProject(Long projectId) {
		TypedQuery<Long> q = getEm()
				.createQuery(
						"select count(s) from Sprint as s where s.project.id=:projectId",
						Long.class);
		q.setParameter("projectId", projectId);
		return q.getSingleResult();
	}

}
