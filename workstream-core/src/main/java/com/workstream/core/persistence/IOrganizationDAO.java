package com.workstream.core.persistence;

import java.util.Collection;

import com.workstream.core.model.Organization;
import com.workstream.core.model.UserX;

public interface IOrganizationDAO extends IGenericDAO<Organization, Long> {

	public abstract boolean isIdentifierExist(String identifier);

	public abstract Long countWithIdentifier(String identifier);

	public abstract Organization findByIdentifier(String identifier);

	public abstract Collection<Organization> filterByUserX(UserX userX);

}
