package com.workstream.core.event;

import org.activiti.engine.delegate.event.ActivitiEntityEvent;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.task.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.workstream.core.model.CoreEvent;
import com.workstream.core.model.CoreEvent.TargetType;

public class CoreTaskCommentEventListener extends
		AbstractCoreActivitiEventListener {
	private Logger logger = LoggerFactory
			.getLogger(CoreTaskCommentEventListener.class);

	public static ActivitiEventType[] EVENT_TYPES = { ActivitiEventType.ENTITY_CREATED };

	@Override
	public CoreEvent onActivitiEntityEvent(ActivitiEntityEvent event) {
		Object entity = event.getEntity();
		if (!(entity instanceof Comment)) {
			return null;
		}
		Comment comment = (Comment) entity;
		CoreEvent cEvent = new CoreEvent();
		cEvent.setEventType("CREATED");
		cEvent.setTargetType(TargetType.COMMENT);
		cEvent.setTargetId(comment.getId());
		cEvent.setParentType(TargetType.TASK);
		cEvent.setParentId(comment.getTaskId());
		cEvent.setUserId(comment.getUserId());
		logger.info("Event dispached: {}", cEvent);

		return cEvent;
	}
}
