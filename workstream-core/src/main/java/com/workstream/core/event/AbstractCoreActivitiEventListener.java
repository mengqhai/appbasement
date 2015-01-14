package com.workstream.core.event;

import org.activiti.engine.delegate.event.ActivitiEntityEvent;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.workstream.core.model.CoreEvent;
import com.workstream.core.service.CoreEventService;

public abstract class AbstractCoreActivitiEventListener implements
		ActivitiEventListener {

	private Logger logger = LoggerFactory
			.getLogger(CoreTaskCommentEventListener.class);

	@Autowired
	protected CoreEventService coreEventService;

	@Override
	public void onEvent(ActivitiEvent event) {
		try {
			if (!(event instanceof ActivitiEntityEvent)) {
				return;
			}
			CoreEvent cEvent = onActivitiEntityEvent((ActivitiEntityEvent) event);
			if (cEvent == null) {
				return;
			}
			coreEventService.saveCoreEvent(cEvent);
		} catch (Exception e) {
			logger.error("Failed on processing activiti event: {}", event, e);
		}
	}

	public abstract CoreEvent onActivitiEntityEvent(ActivitiEntityEvent event);

	@Override
	public boolean isFailOnException() {
		return false;
	}

}
