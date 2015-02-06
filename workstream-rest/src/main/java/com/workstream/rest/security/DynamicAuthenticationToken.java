package com.workstream.rest.security;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.ReflectionUtils;

public class DynamicAuthenticationToken extends
		UsernamePasswordAuthenticationToken {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6546003915233865519L;

	public DynamicAuthenticationToken(Object principal, Object credentials,
			Collection<? extends GrantedAuthority> authorities) {
		super(principal, credentials, authorities);
		Collection<GrantedAuthority> dynAuthorities = new CopyOnWriteArrayList<>(
				authorities);
		Field authField = ReflectionUtils.findField(
				DynamicAuthenticationToken.class, "authorities");
		authField.setAccessible(true);
		ReflectionUtils.setField(authField, this, dynAuthorities);
	}

}
