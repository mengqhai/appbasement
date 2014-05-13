package com.angularjsplay.persistence;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.Query;

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
		return filterFor("project.id", projectId, first, max);
	}

	@Override
	public Long getBacklogCountForProject(Long projectId) {
		return countFilteredFor("project.id", projectId);
	}

	@Override
	public Collection<Backlog> getBacklogsForSprint(Long sprintId) {
		return getBacklogsForSprint(sprintId, 0, Integer.MAX_VALUE);
	}

	@Override
	public Collection<Backlog> getBacklogsForSprint(Long sprintId, int first,
			int max) {
		return filterFor("sprint.id", sprintId, first, max);
	}

	@Override
	public Long getBacklogCountForSprint(Long sprintId) {
		return countFilteredFor("sprint.id", sprintId);
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
