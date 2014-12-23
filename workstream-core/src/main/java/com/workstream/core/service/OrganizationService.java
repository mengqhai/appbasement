package com.workstream.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.model.Organization;
import com.workstream.core.persistence.IOrganizationDAO;

@Service
@Transactional(propagation = Propagation.REQUIRED, value="txManager")
public class OrganizationService {

	@Autowired
	private IOrganizationDAO orgDao;

	//@Transactional(propagation = Propagation.REQUIRED)
	public void createOrg(String name, String identifier, String description) {
		Organization org = new Organization();
		org.setName(name);
		org.setIdentifier(identifier);
		org.setDescription(description);
		orgDao.persist(org);
	}

}
