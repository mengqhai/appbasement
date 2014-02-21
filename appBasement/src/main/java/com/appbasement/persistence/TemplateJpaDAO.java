package com.appbasement.persistence;

import java.sql.Clob;
import java.sql.Connection;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.appbasement.model.Template;
import com.appbasement.util.AppBasementUtil;

@Repository("templateDao")
@Transactional(propagation = Propagation.REQUIRED)
public class TemplateJpaDAO extends GenericJpaDAO<Template, Long> implements
		ITemplateDAO {

	public TemplateJpaDAO() {
	}

	public TemplateJpaDAO(EntityManager em) {
		super(em);
	}

	@Override
	public Date getLastUpdate(String name) {
		Template t = this.findByName(name);
		if (t != null) {
			return t.getLastUpdate();
		} else {
			return null;
		}
	}

	@Override
	public Template findByName(String name) {
		if (name == null || name.equals("")) {
			throw new IllegalArgumentException(
					"Template name is null or empty:" + name);
		}
		TypedQuery<Template> q = getEm().createNamedQuery(
				Template.QUERY_BY_NAME, Template.class);
		q.setParameter("name", name);
		Template result = null;
		try {
			result = q.getSingleResult();
		} catch (NoResultException e) {
		}
		return result;
	}

	@Override
	public void setDefinition(Template template, String definition) {
		Clob clob = template.getDefinition();
		if (clob == null) {
			Connection conn = getEm().unwrap(Connection.class);
			AppBasementUtil.createWriteClob(definition, conn);
		} else {
			AppBasementUtil.updateClob(definition, clob);
		}
	}

	@Override
	public String getDefinition(Template template) {
		Clob clob = template.getDefinition();
		if (clob == null) {
			return null;
		} else {
			return AppBasementUtil.readClob(clob);
		}
	}

}
