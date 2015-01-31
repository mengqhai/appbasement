package com.workstream.rest.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import net.sf.ehcache.Element;
import net.sf.ehcache.search.attribute.AttributeExtractor;
import net.sf.ehcache.search.attribute.AttributeExtractorException;

/**
 * See http://www.tuicool.com/articles/fqARre
 * 
 * @author qinghai
 * 
 */
public class RestTokenUserIdAttributeExtractor implements AttributeExtractor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2217779736481773930L;

	@Override
	public Object attributeFor(Element element, String attributeName)
			throws AttributeExtractorException {
		Object value = element.getObjectValue();
		if (value instanceof SecurityContext) {
			SecurityContext sc = (SecurityContext) value;
			Authentication auth = sc.getAuthentication();
			if (auth == null) {
				return null;
			} else {
				return auth.getName(); // the user id
			}

		} else {
			return null;
		}
	}

}
