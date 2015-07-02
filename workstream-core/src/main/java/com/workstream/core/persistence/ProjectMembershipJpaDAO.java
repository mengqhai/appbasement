package com.workstream.core.persistence;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.CoreConstants;
import com.workstream.core.model.Project;
import com.workstream.core.model.ProjectMembership;

@Repository
@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
public class ProjectMembershipJpaDAO extends
		GenericJpaDAO<ProjectMembership, Long> implements IProjectMembershipDAO {

	@Override
	public Collection<ProjectMembership> filterFor(Project pro, int first,
			int max) {
		return filterFor("project", pro, first, max);
	}

	@Override
	public Collection<ProjectMembership> filterForUser(String userId,
			int first, int max) {
		return filterFor("userId", userId, first, max);
	}

	@Override
	public Long countForUserAndProject(String userId, Project pro) {
		Map<String, Serializable> attributes = new HashMap<String, Serializable>();
		attributes.put("userId", userId);
		attributes.put("project", pro);
		return countFor(attributes);
	}

}
