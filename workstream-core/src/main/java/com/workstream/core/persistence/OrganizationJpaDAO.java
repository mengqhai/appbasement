package com.workstream.core.persistence;

import java.util.Collection;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.CoreConstants;
import com.workstream.core.model.Organization;
import com.workstream.core.model.UserX;

@Repository
@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
public class OrganizationJpaDAO extends GenericJpaDAO<Organization, Long>
		implements IOrganizationDAO {

	@Override
	public Long countWithIdentifier(String identifier) {
		return countFor("identifier", identifier);
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

	@Override
	public Collection<Organization> filterByUserX(UserX userX) {
		return this.filterForContains("users", userX);
	}

}
