package com.workstream.rest.security.exp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import com.workstream.rest.utils.RestUtils;

public class WsSecurityExpressionRoot extends SecurityExpressionRoot implements
		MethodSecurityExpressionOperations {

	private final static Logger log = LoggerFactory
			.getLogger(WsSecurityExpressionRoot.class);

	private Object filterObject;
	private Object returnObject;
	private Object target;

	public WsSecurityExpressionRoot(Authentication a) {
		super(a);
	}

	public String decodeUserId(String userIdBase64) {
		try {
			return RestUtils.decodeUserId(userIdBase64);
		} catch (Exception e) {
			log.warn("Failed to decode user id", e);
			return null;
		}
	}

	@Override
	public void setFilterObject(Object filterObject) {
		this.filterObject = filterObject;
	}

	@Override
	public Object getFilterObject() {
		return filterObject;
	}

	@Override
	public void setReturnObject(Object returnObject) {
		this.returnObject = returnObject;
	}

	@Override
	public Object getReturnObject() {
		return returnObject;
	}

	@Override
	public Object getThis() {
		return target;
	}

}
