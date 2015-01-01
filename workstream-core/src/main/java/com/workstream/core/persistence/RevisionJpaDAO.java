package com.workstream.core.persistence;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.CoreConstants;
import com.workstream.core.model.Revision;

@Repository
@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
public class RevisionJpaDAO extends GenericJpaDAO<Revision, Long> implements
		IRevisionDAO {

	private final Logger log = LoggerFactory.getLogger(RevisionJpaDAO.class);

	protected Map<String, String> createAttributes(String objType, String objId) {
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("objType", objType);
		attributes.put("objId", objId);
		return attributes;
	}

	@Override
	public Collection<Revision> filterFor(String objType, String objId) {
		Map<String, String> attributes = createAttributes(objType, objId);
		return filterFor(attributes, 0, Integer.MAX_VALUE);
	}

	@Override
	public int deleteFor(String objType, String objId) {
		Map<String, String> attributes = createAttributes(objType, objId);
		int count = removeFor(attributes);
		log.info("Deleted {} revisions for {}[{}]", count, objType, objId);
		return count;
	}
}
