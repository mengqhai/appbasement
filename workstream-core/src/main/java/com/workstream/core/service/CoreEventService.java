package com.workstream.core.service;

import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityNotFoundException;

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
import com.workstream.core.exception.ResourceNotFoundException;
import com.workstream.core.model.CoreEvent;
import com.workstream.core.model.CoreEvent.EventType;
import com.workstream.core.model.CoreEvent.TargetType;
import com.workstream.core.model.Notification;
import com.workstream.core.model.Subscription;
import com.workstream.core.persistence.ICoreEventDAO;
import com.workstream.core.persistence.INotificationDAO;
import com.workstream.core.persistence.IOrganizationDAO;
import com.workstream.core.persistence.ISubscriptionDAO;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
public class CoreEventService {

	private Logger logger = LoggerFactory.getLogger(CoreEventService.class);

	@Autowired
	protected ICoreEventDAO coreEventDao;

	@Autowired
	protected ISubscriptionDAO subDao;

	@Autowired
	protected INotificationDAO notDao;

	@Autowired
	public IOrganizationDAO orgDao;

	private EnumSet<TargetType> subscribableTypes = EnumSet.of(TargetType.TASK,
			TargetType.PROCESS, TargetType.PROJECT);

	@Transactional(propagation = Propagation.REQUIRED)
	public void saveCoreEvent(CoreEvent cEvent) {
		coreEventDao.persist(cEvent);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void processAutoSubscribe(CoreEvent cEvent) {
		TargetType tType = cEvent.getTargetType();
		if (!subscribableTypes.contains(tType)) {
			return;
		}
		EventType eType = cEvent.getEventType();
		if (eType == EventType.CREATED) {
			// creator is auto subscribed
			String creator = cEvent.getUserId();
			subscribe(creator, tType, cEvent.getTargetId());
		} else if (tType == TargetType.TASK && eType == EventType.ASSIGNED) {
			// assignee is auto subscribed
			String assignee = cEvent.getAdditionalInfo();
			subscribe(assignee, tType, cEvent.getTargetId());
		}
	}

	public Subscription subscribe(String subscriber, TargetType targetType,
			String targetId) throws AttempBadStateException {
		Collection<Subscription> existings = subDao.filterSubscription(
				subscriber, targetType, targetId);
		if (!existings.isEmpty()) {
			Subscription sub = existings.iterator().next();
			logger.debug("User already subscribed it {}", sub);
			return sub;
		}
		Subscription sub = new Subscription();
		sub.setUserId(subscriber);
		sub.setTargetType(targetType);
		sub.setTargetId(targetId);
		subDao.persist(sub);
		return sub;
	}

	/**
	 * Do this in a background thread. If the event is an end event, this method
	 * will also mark the Subscription(the one matches the target type and id,
	 * not the parent ones) as archived.
	 */
	@Async
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void notifySubscribers(CoreEvent event) {
		Collection<Subscription> mySubscriptions = filterSubscription(
				event.getTargetType(), event.getTargetId());
		Set<String> notifiedUsers = new HashSet<String>();
		for (Subscription sub : mySubscriptions) {
			createNotifcation(sub, event);
			if (event.isEndEvent()) {
				sub.setArchived(true);
			}
			// to prevent the user got notified again by the
			// parent target
			notifiedUsers.add(sub.getUserId());
		}

		/**
		 * People interested in my parents are also interested in me.
		 */
		if (event.getParentType() != null && event.getParentId() != null) {
			Collection<Subscription> parentSubscriptions = filterSubscription(
					event.getParentType(), event.getParentId());
			for (Subscription sub : parentSubscriptions) {
				if (notifiedUsers.contains(sub.getUserId())) {
					// the event has already notified the user
					continue;
				}
				createNotifcation(sub, event);
			}
		}
	}

	/**
	 * 
	 * 
	 * @param sub
	 * @param event
	 * @return
	 */
	public synchronized Notification createNotifcation(Subscription sub,
			CoreEvent event) {
		// has to be synchronized
		// http://stackoverflow.com/questions/10266750/hibernate-unknownserviceexception-unknown-service-requested-as-transaction-comp
		Notification notification = new Notification();
		notification.setEvent(event);
		notification.setOrgId(event.getOrgId());
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

	public Collection<Notification> filterNotificationByUser(String userId,
			boolean onlyUnread) {
		return notDao.filterNotificationByUserId(userId, onlyUnread);
	}

	public void markNotificationRead(Long notificationId) {
		try {
			Notification not = notDao.getReference(notificationId);
			not.setRead(true);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("No such notification");
		}
	}

	public int markAllNotificationReadForUser(String userId) {
		return notDao.markAllNotificationReadForUser(userId);
	}

	public Date getLastNotificationTimeForUser(String userId) {
		return notDao.getLastNotificationTimeForUser(userId);
	}

}
