package com.workstream.core.persistence;

import java.util.Collection;

import com.workstream.core.model.Subscription;

public interface ISubscriptionDAO extends IGenericDAO<Subscription, Long> {

	public abstract Collection<Subscription> filterSubscription(String targetType, String targetId);

	public abstract Collection<Subscription> filterSubscriptionByUser(String userId);

	public abstract Collection<Subscription> filterSubscription(String userId, String targetType,
			String targetId);

}
