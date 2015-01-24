package com.workstream.rest.controller;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.impl.persistence.entity.CommentEntity;
import org.activiti.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.activiti.engine.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.apache.commons.beanutils.ConstructorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.workstream.core.model.Notification;
import com.workstream.core.service.CoreFacadeService;
import com.workstream.rest.exception.DataMappingException;
import com.workstream.rest.model.ArchProcessResponse;
import com.workstream.rest.model.ArchTaskResponse;
import com.workstream.rest.model.EventResponse;
import com.workstream.rest.model.InnerWrapperObj;
import com.workstream.rest.model.NotificationResponse;
import com.workstream.rest.model.SingleValueResponse;

@Api(value = "notifications")
@RestController
@RequestMapping(value = "/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
public class NotificationController {

	private Logger logger = LoggerFactory
			.getLogger(NotificationController.class);

	private final static Map<Class<?>, Class<? extends InnerWrapperObj<?>>> RESPONSE_MAP;
	static {
		Map<Class<?>, Class<? extends InnerWrapperObj<?>>> temp = new HashMap<Class<?>, Class<? extends InnerWrapperObj<?>>>();
		temp.put(CommentEntity.class, EventResponse.class);
		temp.put(HistoricProcessInstanceEntity.class, ArchProcessResponse.class);
		temp.put(HistoricTaskInstanceEntity.class, ArchTaskResponse.class);

		// TODO need to add more?
		RESPONSE_MAP = Collections.unmodifiableMap(temp);
	}

	@Autowired
	private CoreFacadeService core;

	@ApiOperation("Mark the notification as read")
	@RequestMapping(value = "/{notificationId}", method = RequestMethod.PUT)
	public void markRead(@PathVariable Long notificationId) {
		core.getEventService().markNotificationRead(notificationId);
	}

	@ApiOperation("Mark all the notifications of the current user as read")
	@RequestMapping(method = RequestMethod.PUT)
	public void markAllRead() {
		String userId = core.getAuthUserId();
		int count = core.getEventService().markAllNotificationReadForUser(
				userId);
		logger.debug("Marked {} notifications as read.", count);
	}

	@ApiOperation("Mark all the notifications of the current user as read")
	@RequestMapping(value = "/_latestTime", method = RequestMethod.GET)
	public SingleValueResponse getMyLatestUnreadNotificationTime() {
		String userId = core.getAuthUserId();
		Date latestTime = core.getEventService()
				.getLastNotificationTimeForUser(userId);
		return new SingleValueResponse(latestTime);
	}

	@ApiOperation("Retrieve the notifications for the current user")
	@RequestMapping(method = RequestMethod.GET)
	public Collection<NotificationResponse> getMyNotifications(
			@RequestParam(required = false, defaultValue = "true") Boolean onlyUnread) {
		String userId = core.getAuthUserId();
		Collection<Notification> notifications = core.getEventService()
				.filterNotificationByUser(userId, onlyUnread);
		Collection<NotificationResponse> respList = InnerWrapperObj.valueOf(
				notifications, NotificationResponse.class);
		for (NotificationResponse resp : respList) {
			Object target = core.getTargetObj(resp.getEvent());
			if (target == null) {
				logger.error("Unable to fetch the target: {} {}", resp
						.getEvent().getTargetType(), resp.getEvent()
						.getTargetId());
			}

			Class<? extends InnerWrapperObj<?>> targetRespClass = RESPONSE_MAP
					.get(target.getClass());
			if (targetRespClass == null) {
				logger.error("Unable to fetch the target class: {}",
						target.getClass());
			}

			try {
				InnerWrapperObj<?> targetResp = ConstructorUtils
						.invokeConstructor(targetRespClass, target);
				resp.setTarget(targetResp);
			} catch (Exception e) {
				throw new DataMappingException(e.getMessage(), e);
			}
		}

		return respList;
	}

}
