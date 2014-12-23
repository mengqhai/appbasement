package com.workstream.core.persistence;

import org.springframework.stereotype.Repository;

import com.workstream.core.model.Organization;

@Repository
public class OrganizationJpaDAO extends GenericJpaDAO<Organization, Long>
		implements IOrganizationDAO {

}
