package com.workstream.rest.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.util.DigestUtils;

public class RestTokenSecurityContextRepository extends
		HttpSessionSecurityContextRepository {
	CacheManager cacheMgmt = CacheManager.getInstance();

	protected Cache getCache() {
		Cache c = cacheMgmt.getCache(SPRING_SECURITY_CONTEXT_KEY);
		if (c == null) {
			// TODO create the cache
		}
		return c;
	}

	@Override
	public SecurityContext loadContext(
			HttpRequestResponseHolder requestResponseHolder) {
		SecurityContext ctx = null;
		String token = getRestToken(requestResponseHolder.getRequest());
		if (token != null) {
			Element ele = getCache().get(token);
			if (ele != null) {
				ctx = (SecurityContext) ele.getObjectValue();
			}
		}
		if (ctx == null) {
			ctx = super.loadContext(requestResponseHolder);
		}
		return ctx;
	}

	@Override
	public void saveContext(SecurityContext context,
			HttpServletRequest request, HttpServletResponse response) {
		super.saveContext(context, request, response);
		String token = generateRestToken(context.getAuthentication().getName());
		this.getCache().put(new Element(token, context));

		response.addHeader("API_TOKEN", token);
	}

	@Override
	public boolean containsContext(HttpServletRequest request) {
		boolean tokenInCache = false;
		String token = getRestToken(request);
		if (token != null) {
			tokenInCache = getCache().isKeyInCache(token);
		}
		return super.containsContext(request) || tokenInCache;
	}

	public String getRestToken(HttpServletRequest request) {
		return (String) request.getAttribute("api_key");
	}

	public String generateRestToken(String authName) {
		String org = authName + System.currentTimeMillis();
		String token = DigestUtils.md5DigestAsHex(org.getBytes());
		return token;
	}

}
