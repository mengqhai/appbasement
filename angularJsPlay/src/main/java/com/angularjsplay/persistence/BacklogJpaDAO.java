package com.angularjsplay.persistence;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.Query;
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
		TypedQuery<Backlog> q = getEm()
				.createQuery(
						"select b from Backlog as b where b.project.id=:projectId order by b.id desc",
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

	@Override
	public Collection<Backlog> getBacklogsForSprint(Long sprintId) {
		return getBacklogsForSprint(sprintId, 0, Integer.MAX_VALUE);
	}

	@Override
	public Collection<Backlog> getBacklogsForSprint(Long sprintId, int first,
			int max) {
		TypedQuery<Backlog> q = getEm()
				.createQuery(
						"select b from Backlog as b where b.sprint.id=:sprintId order by b.id desc",
						Backlog.class).setParameter("sprintId", sprintId)
				.setFirstResult(first).setMaxResults(max);
		return q.getResultList();
	}

	@Override
	public Long getBacklogCountForSprint(Long sprintId) {
		TypedQuery<Long> q = getEm()
				.createQuery(
						"select count(b) from Backlog as b where b.sprint.id=:sprintId",
						Long.class).setParameter("sprintId", sprintId);
		return q.getSingleResult();
	}

	@Override
	public int unsetSprintOfBacklogsForSprint(Long sprintId) {
		Query q = getEm()
				.createQuery(
						"update Backlog b set b.sprint=null where b.sprint.id=:sprintId");
		q.setParameter("sprintId", sprintId);
		return q.executeUpdate();
	}
}
