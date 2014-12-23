package com.workstream.core.persistence;

import com.workstream.core.model.Organization;

public interface IOrganizationDAO extends IGenericDAO<Organization, Long> {

	public abstract boolean isIdentifierExist(String identifier);

	public abstract Long countWithIdentifier(String identifier);

}
