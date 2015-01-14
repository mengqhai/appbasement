package com.workstream.core.service;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.CoreConstants;
import com.workstream.core.exception.AttempBadStateException;
import com.workstream.core.model.CoreEvent;
import com.workstream.core.model.CoreEvent.TargetType;
import com.workstream.core.model.Notification;
import com.workstream.core.model.Subscription;
import com.workstream.core.persistence.ICoreEventDAO;
import com.workstream.core.persistence.INotificationDAO;
import com.workstream.core.persistence.ISubscriptionDAO;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
public class CoreEventService {

	@SuppressWarnings("unused")
	private Logger logger = LoggerFactory.getLogger(CoreEventService.class);

	@Autowired
	protected ICoreEventDAO coreEventDao;

	@Autowired
	protected ISubscriptionDAO subDao;

	@Autowired
	protected INotificationDAO notDao;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveCoreEvent(CoreEvent cEvent) {
		coreEventDao.persist(cEvent);
	}

	public Subscription subscribe(String subscriber, TargetType targetType,
			String targetId) throws AttempBadStateException {
		Collection<Subscription> existings = subDao.filterSubscription(
				subscriber, targetType, targetId);
		if (!existings.isEmpty()) {
			throw new AttempBadStateException("User already subscribed it");
		}
		Subscription sub = new Subscription();
		sub.setUserId(subscriber);
		sub.setTargetType(targetType);
		sub.setTargetId(targetId);
		subDao.persist(sub);
		return sub;
	}

	/**
	 * Do this in a background thread.
	 */
	@Async
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void notifySubscribers(CoreEvent event) {
		Collection<Subscription> mySubscriptions = filterSubscription(
				event.getTargetType(), event.getTargetId());
		for (Subscription sub : mySubscriptions) {
			createNotifcation(sub, event);
		}

		/**
		 * People interested in my parents are also interested in me.
		 */
		if (event.getParentType() != null && event.getParentId() != null) {
			Collection<Subscription> parentSubscriptions = filterSubscription(
					event.getParentType(), event.getParentId());
			for (Subscription sub : parentSubscriptions) {
				createNotifcation(sub, event);
			}
		}
	}

	public Notification createNotifcation(Subscription sub, CoreEvent event) {
		Notification notification = new Notification();
		notification.setEvent(event);
		notification.setSub(sub);
		notification.setUserId(sub.getUserId());
		notDao.persist(notification);
		return notification;
	}

	public Collection<Subscription> filterSubscription(TargetType targetType,
			String targetId) {
		return subDao.filterSubscription(targetType, targetId);
	}

	public Collection<Subscription> filterSubscriptionByUser(String userId) {
		return subDao.filterSubscriptionByUser(userId);
	}

}
