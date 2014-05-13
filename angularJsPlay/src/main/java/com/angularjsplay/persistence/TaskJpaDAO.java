package com.angularjsplay.persistence;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

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

	@Override
	public Collection<Task> getTasksForBacklog(Long backlogId) {
		return getTasksForBacklog(backlogId, 0, Integer.MAX_VALUE);
	}

	@Override
	public Collection<Task> getTasksForBacklog(Long backlogId, int first,
			int max) {
		TypedQuery<Task> q = getEm()
				.createQuery(
						"select t from Task as t where t.backlog.id=:backlogId order by t.id desc",
						Task.class).setParameter("backlogId", backlogId)
				.setFirstResult(first).setMaxResults(max);
		return q.getResultList();
	}

	@Override
	public Long getTaskCountForBacklog(Long backlogId) {
		TypedQuery<Long> q = getEm().createQuery(
				"select count(t) from Task as t where t.backlog.id=:backlogId",
				Long.class);
		q.setParameter("backlogId", backlogId);
		return q.getSingleResult();
	}

}
