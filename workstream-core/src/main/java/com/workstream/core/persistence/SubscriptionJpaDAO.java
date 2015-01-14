package com.workstream.core.persistence;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.CoreConstants;
import com.workstream.core.model.Subscription;

@Repository
@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
public class SubscriptionJpaDAO extends GenericJpaDAO<Subscription, Long>
		implements ISubscriptionDAO {

	@Override
	public Collection<Subscription> filterSubscription(String targetType,
			String targetId) {
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("targetId", targetId);
		attributes.put("targetType", targetType);
		return this.filterFor(attributes, 0, Integer.MAX_VALUE);
	}

	@Override
	public Collection<Subscription> filterSubscriptionByUser(String userId) {
		return this.filterFor("userId", userId, 0, Integer.MAX_VALUE);
	}

	@Override
	public Collection<Subscription> filterSubscription(String userId,
			String targetType, String targetId) {
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("targetId", targetId);
		attributes.put("targetType", targetType);
		attributes.put("userId", userId);
		return this.filterFor(attributes, 0, Integer.MAX_VALUE);
	}
}
