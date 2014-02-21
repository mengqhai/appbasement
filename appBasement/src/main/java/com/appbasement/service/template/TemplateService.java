package com.appbasement.service.template;

import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.velocity.VelocityEngineUtils;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class TemplateService implements ITemplateService {

	@Autowired
	VelocityEngine velocityEngine;

	@Override
	public String mergeTemplateToString(String name, String encoding,
			Map<String, Object> model) {
		return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
				name, encoding, model);
	}

}
