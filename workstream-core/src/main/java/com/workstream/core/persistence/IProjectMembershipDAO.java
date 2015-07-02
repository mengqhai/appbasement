package com.workstream.core.persistence;

import java.util.Collection;

import com.workstream.core.model.Project;
import com.workstream.core.model.ProjectMembership;

public interface IProjectMembershipDAO extends
		IGenericDAO<ProjectMembership, Long> {

	public abstract Collection<ProjectMembership> filterFor(Project pro,
			int first, int max);

	public abstract Collection<ProjectMembership> filterForUser(String userId, int first, int max);

	public abstract Long countForUserAndProject(String userId, Project pro);

	public abstract ProjectMembership getProjectMemebership(String userId, Project pro);

}
