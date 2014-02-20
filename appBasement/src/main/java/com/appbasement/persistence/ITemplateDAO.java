package com.appbasement.persistence;

import java.util.Date;

import com.appbasement.model.Template;

public interface ITemplateDAO extends IGenericDAO<Template, Long> {
	
	public Template findByName(String name);

	public abstract Date getLastUpdate(String name);
	
}
