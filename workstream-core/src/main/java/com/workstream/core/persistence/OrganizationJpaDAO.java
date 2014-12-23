package com.workstream.core.persistence;

import org.springframework.stereotype.Repository;

import com.workstream.core.model.Organization;

@Repository
public class OrganizationJpaDAO extends GenericJpaDAO<Organization, Long>
		implements IOrganizationDAO {

	@Override
	public Long countWithIdentifier(String identifier) {
		return countFilteredFor("identifier", identifier);
	}

	@Override
	public boolean isIdentifierExist(String identifier) {
		if (countWithIdentifier(identifier) > 0L) {
			return true;
		} else {
			return false;
		}
	}

}
