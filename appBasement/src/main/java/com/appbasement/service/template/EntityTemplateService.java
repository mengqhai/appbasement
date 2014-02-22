package com.appbasement.service.template;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.appbasement.persistence.ITemplateDAO;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class EntityTemplateService implements IEntityTemplateService {

	@Autowired
	protected ITemplateDAO templateDao;

}
