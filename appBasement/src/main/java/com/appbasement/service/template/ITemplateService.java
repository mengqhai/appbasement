package com.appbasement.service.template;

import java.util.Map;

public interface ITemplateService {

	public abstract String mergeTemplateToString(String name, String encoding,
			Map<String, Object> model);

}
