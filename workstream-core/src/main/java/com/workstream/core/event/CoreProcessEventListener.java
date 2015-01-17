package com.workstream.core.event;

import org.activiti.engine.delegate.event.ActivitiEntityEvent;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.workstream.core.model.CoreEvent;
import com.workstream.core.model.CoreEvent.EventType;
import com.workstream.core.model.CoreEvent.TargetType;

public class CoreProcessEventListener extends AbstractCoreActivitiEventListener {
	private static Logger log = LoggerFactory
			.getLogger(CoreProcessEventListener.class);

	public static ActivitiEventType[] EVENT_TYPES = {
			ActivitiEventType.ENTITY_CREATED,
			ActivitiEventType.ENTITY_ACTIVATED,
			ActivitiEventType.ENTITY_DELETED };

	@Override
	public CoreEvent onActivitiEntityEvent(ActivitiEntityEvent event) {
		Object entity = event.getEntity();
		if (!(entity instanceof ProcessInstance)) {
			return null;
		}
		ProcessInstance pi = (ProcessInstance) entity;
		CoreEvent cEvent = new CoreEvent();
		cEvent.setTargetId(pi.getId());
		cEvent.setTargetType(TargetType.PROCESS);
		cEvent.setParentType(TargetType.ORG);
		cEvent.setParentId(pi.getTenantId());
		switch (event.getType()) {
		case ENTITY_CREATED:
			cEvent.setEventType(EventType.CREATED);
			break;
		case ENTITY_ACTIVATED:
			cEvent.setEventType(EventType.ARCHIVED);
			break;
		case ENTITY_DELETED:
			if (pi.isEnded()) {
				cEvent.setEventType(EventType.COMPLETED);
			} else {
				cEvent.setEventType(EventType.DELETED);
			}
			break;
		default:
			return null;
		}

		log.info("Event dispatched: {}", cEvent);
		cEvent.setUserId(Authentication.getAuthenticatedUserId());
		if (pi.getTenantId() != null && !pi.getTenantId().isEmpty()) {
			try {
				cEvent.setOrgId(Long.valueOf(pi.getTenantId()));
			} catch (NumberFormatException e) {
			}
		}
		return cEvent;
	}
}
