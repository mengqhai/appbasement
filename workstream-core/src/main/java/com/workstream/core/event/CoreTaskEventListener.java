package com.workstream.core.event;

import org.activiti.engine.delegate.event.ActivitiEntityEvent;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.workstream.core.model.CoreEvent;
import com.workstream.core.model.CoreEvent.EventType;
import com.workstream.core.model.CoreEvent.TargetType;

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
		if (event.getType() == ActivitiEventType.TASK_CREATED) {
			cEvent.setEventType(EventType.CREATED);
		} else if (event.getType() == ActivitiEventType.TASK_COMPLETED) {
			cEvent.setEventType(EventType.COMPLETED);
		} else {
			cEvent.setEventType(EventType.ASSIGNED);
			cEvent.setAdditionalInfo(task.getAssignee());
		}

		cEvent.setTargetId(task.getId());
		cEvent.setTargetType(TargetType.TASK);
		cEvent.setUserId(Authentication.getAuthenticatedUserId());
		if (task.getProcessInstanceId() != null) {
			cEvent.setParentType(TargetType.PROCESS);
			cEvent.setParentId(task.getProcessInstanceId());
		} else if (task.getCategory() != null) {
			cEvent.setParentType(TargetType.PROJECT);
			cEvent.setParentId(task.getCategory());
		}
		logger.info("Event dispatched: {}", cEvent);

		if (task.getTenantId() != null && !task.getTenantId().isEmpty()) {
			try {
				cEvent.setOrgId(Long.valueOf(task.getTenantId()));
			} catch (NumberFormatException e) {
			}
		}
		return cEvent;
	}

}
