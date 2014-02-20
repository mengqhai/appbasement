package com.appbasement.persistence;

import javax.persistence.EntityManager;

import com.appbasement.model.Template;

public class TemplateJpaDAO extends GenericJpaDAO<Template, Long> implements
		ITemplateDAO {

	public TemplateJpaDAO() {
	}

	public TemplateJpaDAO(EntityManager em) {
		super(em);
	}

}
