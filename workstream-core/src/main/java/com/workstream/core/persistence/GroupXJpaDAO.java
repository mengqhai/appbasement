package com.workstream.core.persistence;

import java.util.Collection;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.CoreConstants;
import com.workstream.core.model.GroupX;
import com.workstream.core.model.Organization;

@Repository
@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
public class GroupXJpaDAO extends GenericJpaDAO<GroupX, Long> implements
		IGroupXDAO {

	@Override
	public GroupX findByGroupId(String groupId) {
		return findFor("groupId", groupId);
	}

	@Override
	public Collection<GroupX> filterFor(Organization org) {
		return filterFor("org", org, 0, Integer.MAX_VALUE);
	}

}
