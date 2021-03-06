package com.workstream.core.service;

import java.util.Collection;
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
import com.workstream.core.exception.BeanPropertyException;
import com.workstream.core.model.Organization;
import com.workstream.core.model.UserX;
import com.workstream.core.persistence.IOrganizationDAO;

@Service
@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
public class OrganizationService {

	private final Logger log = LoggerFactory
			.getLogger(OrganizationService.class);

	@Autowired
	private IOrganizationDAO orgDao;

	/**
	 * Append random suffix if the given identifier is not unique.
	 * 
	 * @param originalIdentifier
	 * @return
	 */
	private String uniqueIdentifier(String originalIdentifier) {
		StringBuilder builder = new StringBuilder(originalIdentifier);
		while (orgDao.isIdentifierExist(builder.toString())) {
			builder.append("-").append(RandomStringUtils.randomAlphanumeric(4));
		}
		return builder.toString();
	}

	// @Transactional(propagation = Propagation.REQUIRED)
	public Organization createOrg(String name, String identifier,
			String description) {
		Organization org = new Organization();
		org.setIdentifier(uniqueIdentifier(identifier));
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

	public void updateOrg(Long id, Map<String, Object> props)
			throws BeanPropertyException {
		Organization org = orgDao.findById(id);
		if (org == null) {
			log.warn("Tring to update non-existing org: id={}", id);
		}
		//
		String newIdentifier = (String) props.get("identifier");
		if (newIdentifier != null && !newIdentifier.equals(org.getIdentifier())) {
			String uniqueIdentifier = uniqueIdentifier(newIdentifier);
			props.put("identifier", uniqueIdentifier);
		}

		try {
			BeanUtils.populate(org, props);
		} catch (Exception e) {
			log.error("Failed to populate the props to org: {}", props, e);
			throw new BeanPropertyException(e);
		}
		log.debug("Successfully update org: {}", org);
	}

	public Organization findOrgById(Long id) {
		return orgDao.findById(id);
	}

	public Organization findOrgByIdEagerUsers(Long id) {
		Organization org = findOrgById(id);
		if (org != null) {
			org.getUsers().size();
		}
		return org;
	}

	public Organization findOrgByIdentifier(String identifier) {
		return orgDao.findByIdentifier(identifier);
	}

	public void removeOrg(Organization org) {
		org = orgDao.reattachIfNeeded(org, org.getId());
		orgDao.remove(org);
		log.info("Deleted org {} ", org);
	}

	public void removeOrg(String identifier) {
		Organization org = findOrgByIdentifier(identifier);
		if (org != null) {
			removeOrg(org);
		}
	}

	public void removeOrg(Long id) {
		Organization org = orgDao.findById(id);
		if (org != null) {
			removeOrg(id);
		}
	}

	public Collection<Organization> filterOrg(UserX userX) {
		return orgDao.filterByUserX(userX);
	}

	public void userJoinOrg(UserX userX, Organization org) {
		org = orgDao.reattachIfNeeded(org, org.getId());
		org.getUsers().add(userX);
	}

	public void userLeaveOrg(UserX userX, Organization org) {
		org = orgDao.reattachIfNeeded(org, org.getId());
		org.getUsers().remove(userX);
	}

	public boolean isUserInOrg(UserX userX, Organization org) {
		org = orgDao.reattachIfNeeded(org, org.getId());
		return org.getUsers().contains(userX);
	}

}
