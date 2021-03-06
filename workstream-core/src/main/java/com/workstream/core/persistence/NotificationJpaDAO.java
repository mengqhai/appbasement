package com.workstream.core.persistence;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.CoreConstants;
import com.workstream.core.model.Notification;

@Repository
@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
public class NotificationJpaDAO extends GenericJpaDAO<Notification, Long>
		implements INotificationDAO {

	@Override
	public Collection<Notification> filterNotificationByUserId(String userId,
			boolean onlyUnread, int first, int max) {
		Map<String, Serializable> attri = new HashMap<String, Serializable>();
		attri.put("userId", userId);
		if (onlyUnread) {
			attri.put("read", false);
		}
		// else get all
		return this.filterFor(attri, first, max);
	}

	@Override
	public Long countNotificationByUserId(String userId, boolean onlyUnread) {
		Map<String, Serializable> attri = new HashMap<String, Serializable>();
		attri.put("userId", userId);
		if (onlyUnread) {
			attri.put("read", false);
		}
		// else get all
		return this.countFor(attri);
	}

	@Override
	public Date getLastNotificationTimeForUser(String userId) {
		Query q = getEm()
				.createQuery(
						"select n.createdAt from Notification n where n.userId = :userId and n.read = false order by n.id desc");
		q.setMaxResults(1);
		q.setParameter("userId", userId);
		try {
			return (Date) q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public int markAllNotificationReadForUser(String userId) {
		Query q = getEm()
				.createQuery(
						"update Notification n set n.read = true where n.userId= :userId and n.read = false");
		q.setParameter("userId", userId);
		return q.executeUpdate();
	}

}
