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
	public Collection<Task> getTasksForBacklog(Long sprintId) {
		return getTasksForSprint(sprintId, 0, Integer.MAX_VALUE);
	}

	@Override
	public Collection<Task> getTasksForSprint(Long sprintId, int first,
			int max) {
		return filterFor("sprint.id", sprintId, first, max);
	}

	@Override
	public Long getTaskCountForSprint(Long sprintId) {
		return countFilteredFor("sprint.id", sprintId);
	}

}
