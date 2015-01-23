package com.workstream.core.event;

import java.util.List;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.event.ActivitiEntityEvent;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.impl.persistence.entity.CommentEntity;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.workstream.core.model.CoreEvent;
import com.workstream.core.model.CoreEvent.EventType;
import com.workstream.core.model.CoreEvent.TargetType;

public class CoreTaskCommentEventListener extends
		AbstractCoreActivitiEventListener {
	private Logger logger = LoggerFactory
			.getLogger(CoreTaskCommentEventListener.class);

	public static ActivitiEventType[] EVENT_TYPES = { ActivitiEventType.ENTITY_CREATED };

	@Autowired
	public TaskService taskSer;

	@Override
	public CoreEvent onActivitiEntityEvent(ActivitiEntityEvent event) {
		Object entity = event.getEntity();
		if (!(entity instanceof Comment)) {
			return null;
		}
		CommentEntity comment = (CommentEntity) entity;
		List<String> messageParts = comment.getMessageParts();
		if (messageParts.size() == 2 && messageParts.get(1).equals("assignee")) {
			// ignore comments about assignment
			// because we already have assignment task events dispatched
			return null;
		}

		CoreEvent cEvent = new CoreEvent();
		cEvent.setEventType(EventType.CREATED);
		cEvent.setTargetType(TargetType.COMMENT);
		cEvent.setTargetId(comment.getId());
		cEvent.setParentType(TargetType.TASK);
		cEvent.setParentId(comment.getTaskId());
		cEvent.setUserId(comment.getUserId());
		logger.info("Event dispached: {}", cEvent);

		// to set the org id
		Task task = taskSer.createTaskQuery().taskId(comment.getTaskId())
				.singleResult();
		if (task != null && task.getTenantId() != null
				&& !task.getTenantId().isEmpty()) {
			try {
				cEvent.setOrgId(Long.valueOf(task.getTenantId()));
			} catch (NumberFormatException e) {
			}
		}

		return cEvent;
	}
}
