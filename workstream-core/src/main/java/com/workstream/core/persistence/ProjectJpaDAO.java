package com.workstream.core.persistence;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.CoreConstants;
import com.workstream.core.model.Organization;
import com.workstream.core.model.Project;
import com.workstream.core.model.Project.ProjectVisibility;

@Repository
@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
public class ProjectJpaDAO extends GenericJpaDAO<Project, Long> implements
		IProjectDAO {

	@Override
	public Collection<Project> filterFor(Organization org, int first, int max) {
		return filterFor("org", org, first, max);
	}

	@Override
	public Long countFor(Organization org) {
		return countFor("org", org);
	}

	@Override
	public Collection<Project> filterFor(Organization org,
			ProjectVisibility visibility, int first, int max) {
		Map<String, Serializable> attributes = new HashMap<String, Serializable>();
		attributes.put("org", org);
		attributes.put("visibility", visibility);
		return this.filterFor(attributes, first, max);
	}

	/**
	 * Select the projects that are visible to the user in the given org
	 * 
	 * 
	 * @param org
	 * @param userId
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Collection<Project> filterForUserVisiableInOrg(Organization org,
			String userId) {
		Query q = getEm()
				.createQuery(
						"select p from Project as p, ProjectMembership m where p.org=:org and (p.visibility=:visibility or m.userId=:userId)");
		q.setParameter("org", org);
		q.setParameter("visibility", ProjectVisibility.OPEN);
		q.setParameter("userId", userId);
		return q.getResultList();
	}

}
