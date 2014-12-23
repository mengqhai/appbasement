package com.workstream.core.service;

import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.CoreConstants;
import com.workstream.core.model.Organization;
import com.workstream.core.persistence.IOrganizationDAO;

@Service
@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
public class OrganizationService {

	private final Logger log = LoggerFactory
			.getLogger(OrganizationService.class);

	@Autowired
	private IOrganizationDAO orgDao;

	// @Transactional(propagation = Propagation.REQUIRED)
	public Organization createOrg(String name, String identifier,
			String description) {
		Organization org = new Organization();
		StringBuilder builder = new StringBuilder(identifier);
		while (orgDao.isIdentifierExist(builder.toString())) {
			builder.append("-").append(RandomStringUtils.randomAlphanumeric(4));
		}
		org.setIdentifier(builder.toString());
		org.setName(name);
		org.setDescription(description);
		try {
			orgDao.persist(org);
		} catch (javax.persistence.PersistenceException ex) {
			log.error("Failed to save org: {}", org, ex);
		}

		log.debug("Successfully saved org: {}", org);
		return org;
	}

	public void updateOrg(Long id, Map<String, ? extends Object> props) {
		Organization org = orgDao.findById(id);
		if (org == null) {
			log.warn("Tring to update non-existing org: id={}", id);
		}
		try {
			BeanUtils.populate(org, props);
		} catch (Exception e) {
			log.error("Failed to populate the props to org: {}", props, e);
			throw new RuntimeException(e);
		}
		log.debug("Successfully update org: {}", org);
	}

}
