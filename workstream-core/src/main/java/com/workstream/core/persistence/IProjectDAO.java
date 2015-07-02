package com.workstream.core.persistence;

import java.util.Collection;

import com.workstream.core.model.Organization;
import com.workstream.core.model.Project;
import com.workstream.core.model.Project.ProjectVisibility;

public interface IProjectDAO extends IGenericDAO<Project, Long> {

	public abstract Collection<Project> filterFor(Organization org, int first,
			int max);

	public abstract Long countFor(Organization org);

	public abstract Collection<Project> filterFor(Organization org, ProjectVisibility visibility, int first,
			int max);

	public abstract Collection<Project> filterForUserVisiableInOrg(Organization org, String userId);

}
