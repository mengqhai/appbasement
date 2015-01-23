package com.workstream.core.persistence;

import java.util.Collection;

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
	public Collection<Notification> filterNotificationByUserId(String userId) {
		return this.filterFor("userId", userId, 0, Integer.MAX_VALUE);
	}

}
