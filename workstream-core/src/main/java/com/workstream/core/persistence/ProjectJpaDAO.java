package com.workstream.core.persistence;

import java.util.Collection;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.CoreConstants;
import com.workstream.core.model.Organization;
import com.workstream.core.model.Project;

@Repository
@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
public class ProjectJpaDAO extends GenericJpaDAO<Project, Long> implements
		IProjectDAO {

	@Override
	public Collection<Project> filterFor(Organization org) {
		return filterFor("org", org, 0, Integer.MAX_VALUE);
	}

}
