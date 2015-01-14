package com.workstream.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.CoreConstants;
import com.workstream.core.model.CoreEvent;
import com.workstream.core.persistence.ICoreEventDAO;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
public class CoreEventService {

	@SuppressWarnings("unused")
	private Logger logger = LoggerFactory.getLogger(CoreEventService.class);

	@Autowired
	protected ICoreEventDAO coreEventDao;

	public void saveCoreEvent(CoreEvent cEvent) {
		coreEventDao.persist(cEvent);
	}

}
