package com.workstream.core.event;

import org.activiti.engine.delegate.event.ActivitiEntityEvent;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.workstream.core.model.CoreEvent;

public class CoreTaskEventListener extends AbstractCoreActivitiEventListener {

	private Logger logger = LoggerFactory
			.getLogger(CoreTaskEventListener.class);

	public static ActivitiEventType[] EVENT_TYPES = {
			ActivitiEventType.TASK_CREATED, ActivitiEventType.TASK_COMPLETED,
			ActivitiEventType.TASK_ASSIGNED };

	@Override
	public CoreEvent onActivitiEntityEvent(ActivitiEntityEvent event) {
		Object entityObj = event.getEntity();
		if (!(entityObj instanceof Task)) {
			return null;
		}
		Task task = (Task) entityObj;
		CoreEvent cEvent = new CoreEvent();
		cEvent.setEventType(event.getType().toString());
		cEvent.setTargetId(task.getId());
		cEvent.setTargetType("TASK");
		cEvent.setUserId(Authentication.getAuthenticatedUserId());
		if (task.getProcessInstanceId() != null) {
			cEvent.setParentType("PROCESS");
			cEvent.setParentId(task.getProcessInstanceId());
		} else if (task.getCategory() != null) {
			cEvent.setParentType("PROJECT");
			cEvent.setParentId(task.getCategory());
		}
		logger.info("Event dispatched: {}", cEvent);
		return cEvent;
	}

}
