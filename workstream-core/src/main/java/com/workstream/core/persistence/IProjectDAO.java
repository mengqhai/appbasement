package com.workstream.core.persistence;

import java.util.Collection;

import com.workstream.core.model.Organization;
import com.workstream.core.model.Project;

public interface IProjectDAO extends IGenericDAO<Project, Long> {

	public abstract Collection<Project> filterFor(Organization org, int first,
			int max);

	public abstract Long countFor(Organization org);

}
