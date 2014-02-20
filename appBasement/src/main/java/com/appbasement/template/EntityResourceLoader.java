package com.appbasement.template;

import java.io.InputStream;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

public class EntityResourceLoader extends ResourceLoader {

	public EntityResourceLoader() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(ExtendedProperties configuration) {
		// TODO Auto-generated method stub

	}

	@Override
	public InputStream getResourceStream(String source)
			throws ResourceNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSourceModified(Resource resource) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long getLastModified(Resource resource) {
		// TODO Auto-generated method stub
		return 0;
	}

}
