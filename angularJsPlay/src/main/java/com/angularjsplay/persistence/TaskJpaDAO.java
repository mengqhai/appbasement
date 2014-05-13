package com.angularjsplay.persistence;

import java.util.Collection;

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

	@Override
	public Collection<Task> getTasksForBacklog(Long backlogId) {
		return getTasksForBacklog(backlogId, 0, Integer.MAX_VALUE);
	}

	@Override
	public Collection<Task> getTasksForBacklog(Long backlogId, int first,
			int max) {
		return filterFor("backlog.id", backlogId, first, max);
	}

	@Override
	public Long getTaskCountForBacklog(Long backlogId) {
		return countFilteredFor("backlog.id", backlogId);
	}

}
