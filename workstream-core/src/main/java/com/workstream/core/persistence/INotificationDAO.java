package com.workstream.core.persistence;

import java.util.Collection;
import java.util.Date;

import com.workstream.core.model.Notification;

public interface INotificationDAO extends IGenericDAO<Notification, Long> {

	public abstract Collection<Notification> filterNotificationByUserId(
			String userId, boolean onlyUnread, int first, int max);

	public abstract int markAllNotificationReadForUser(String userId);

	public abstract Date getLastNotificationTimeForUser(String userId);

	public abstract Long countNotificationByUserId(String userId, boolean onlyUnread);

}
