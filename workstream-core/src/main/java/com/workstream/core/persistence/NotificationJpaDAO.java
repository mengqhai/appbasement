package com.workstream.core.persistence;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
			boolean onlyUnread) {
		Map<String, Serializable> attri = new HashMap<String, Serializable>();
		attri.put("userId", userId);
		if (onlyUnread) {
			attri.put("read", false);
		}
		// else get all
		return this.filterFor(attri, 0, Integer.MAX_VALUE);
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
