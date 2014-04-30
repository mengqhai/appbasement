package com.angularjsplay.persistence;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

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

	@Override
	public Collection<Backlog> getBacklogsForProject(Long projectId) {
		return getBacklogsForProject(projectId, 0, Integer.MAX_VALUE);
	}

	/**
	 * With paging support.
	 * 
	 * @param projectId
	 * @param max
	 * @return
	 */
	@Override
	public Collection<Backlog> getBacklogsForProject(Long projectId, int first,
			int max) {
		TypedQuery<Backlog> q = getEm().createQuery(
				"select b from Backlog as b where b.project.id=:projectId",
				Backlog.class);
		q.setParameter("projectId", projectId);
		q.setFirstResult(first);
		q.setMaxResults(max);
		return q.getResultList();
	}

	@Override
	public Long getBacklogCountForProject(Long projectId) {
		TypedQuery<Long> q = getEm()
				.createQuery(
						"select count(b) from Backlog as b where b.project.id=:projectId",
						Long.class);
		q.setParameter("projectId", projectId);
		return q.getSingleResult();
	}

}
