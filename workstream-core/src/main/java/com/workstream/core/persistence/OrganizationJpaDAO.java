package com.workstream.core.persistence;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.CoreConstants;
import com.workstream.core.model.Organization;

@Repository
@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
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

	@Override
	public Organization findByIdentifier(String identifier) {
		Organization result = findFor("identifier", identifier);
		return result;
	}

}
