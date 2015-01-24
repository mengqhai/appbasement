package com.workstream.core.persistence;

import java.util.Collection;

import com.workstream.core.model.Notification;

public interface INotificationDAO extends IGenericDAO<Notification, Long> {

	public abstract Collection<Notification> filterNotificationByUserId(
			String userId, boolean onlyUnread);

	public abstract int markAllNotificationReadForUser(String userId);

}
