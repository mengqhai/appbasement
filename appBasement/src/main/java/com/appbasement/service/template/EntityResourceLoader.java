package com.appbasement.service.template;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;

import com.appbasement.model.Template;
import com.appbasement.persistence.ITemplateDAO;

public class EntityResourceLoader extends ResourceLoader {

	@Autowired
	protected ITemplateDAO dao;

	public EntityResourceLoader() {
	}

	@Override
	public void init(ExtendedProperties configuration) {
	}

	@Override
	public InputStream getResourceStream(String name)
			throws ResourceNotFoundException {
		Template template = dao.findByName(name);
		if (template != null) {
			try {
				return template.getDefinition().getAsciiStream();
			} catch (SQLException e) {
				throw new ResourceNotFoundException("Fail to load entity "
						+ name, e);
			}
		} else {
			throw new ResourceNotFoundException("No template entity named "
					+ name);
		}
	}

	protected long readLastUpdate(Resource resource) {
		Date lastUpdate = dao.getLastUpdate(resource.getName());
		if (lastUpdate != null) {
			return lastUpdate.getTime();
		} else {
			throw new ResourceNotFoundException(
					"Fail to load entity last update time:"
							+ resource.getName());
		}
	}

	@Override
	public boolean isSourceModified(Resource resource) {
		return resource.getLastModified() != this.readLastUpdate(resource);
	}

	@Override
	public long getLastModified(Resource resource) {
		return dao.getLastUpdate(resource.getName()).getTime();
	}

	public ITemplateDAO getDao() {
		return dao;
	}

	public void setDao(ITemplateDAO dao) {
		this.dao = dao;
	}

}
