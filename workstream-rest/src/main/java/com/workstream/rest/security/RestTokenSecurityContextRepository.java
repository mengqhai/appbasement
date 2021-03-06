package com.workstream.rest.security;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Query;
import net.sf.ehcache.search.Result;
import net.sf.ehcache.search.Results;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SaveContextOnUpdateOrErrorResponseWrapper;

import com.workstream.core.exception.ConfigurationException;
import com.workstream.rest.RestConstants;

public class RestTokenSecurityContextRepository extends
		HttpSessionSecurityContextRepository {
	private static final Logger log = LoggerFactory
			.getLogger(RestTokenSecurityContextRepository.class);

	CacheManager cacheMgmt = CacheManager.getInstance();

	protected Cache getCache() {
		Cache c = cacheMgmt.getCache(SPRING_SECURITY_CONTEXT_KEY);
		if (c == null) {
			throw new ConfigurationException("Unable to find cache named "
					+ SPRING_SECURITY_CONTEXT_KEY + ", check you ehcache.xml");
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
		if (response instanceof SaveContextOnUpdateOrErrorResponseWrapper) {
			super.saveContext(context, request, response);
		}

		if (context.getAuthentication() != null
				&& !(context.getAuthentication() instanceof AnonymousAuthenticationToken)) {
			String token = response.getHeader(RestConstants.API_KEY);
			// the response is somehow read-only at this stage
			// so someone must generated the api_key and put it in the response
			// header
			if (token == null) {
				String reqToken = getRestToken(request);
				if (reqToken != null) {
					return; // already saved in cache so don't do it again
				}

				log.warn("No token is set in the response header {}",
						RestConstants.API_KEY);
				// throw new ConfigurationException(
				// "No token is set in the response header "
				// + RestConstants.API_KEY);
			}
			if (token != null && !this.getCache().isKeyInCache(token)) {
				log.info("Saved security context for token {} ", token);
				this.getCache().put(new Element(token, context));
			}
		}
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
		return (String) request.getParameter(RestConstants.API_KEY);
	}

	public void clearSecurityContextByToken(HttpServletRequest request) {
		String token = getRestToken(request);
		if (token != null) {
			getCache().remove(token);
		}
	}

	public void clearSecurityContextByToken(String token) {
		getCache().remove(token);
	}

	public int clearSecurityContextByUser(String userId) {
		Query q = prepareQueryForUser(userId);
		q.includeValues();
		q.includeKeys();
		Results r = q.execute();
		List<Result> resultList = r.all();
		List<String> keys = new ArrayList<String>(resultList.size());
		List<SecurityContext> ctxList = new ArrayList<SecurityContext>(r.size());
		for (Result result : resultList) {
			SecurityContext ctx = (SecurityContext) result.getValue();
			ctxList.add(ctx);
			keys.add((String) result.getKey());
		}

		for (SecurityContext ctx : ctxList) {
			ctx.setAuthentication(null);
		}
		getCache().removeAll(keys);
		return keys.size();
	}

	protected Query prepareQueryForUser(String userId) {
		Query q = getCache().createQuery();
		Attribute<String> userIdAtt = getCache().getSearchAttribute("userId");
		q.addCriteria(userIdAtt.eq(userId));
		return q;
	}

	public List<SecurityContext> getSecurityContextByUser(String userId) {
		Query q = prepareQueryForUser(userId);
		q.includeValues();
		Results r = q.execute();
		List<Result> resultList = r.all();
		List<SecurityContext> ctxList = new ArrayList<SecurityContext>(r.size());
		for (Result result : resultList) {
			SecurityContext ctx = (SecurityContext) result.getValue();
			ctxList.add(ctx);
		}
		return ctxList;
	}

	/**
	 * Only supports DynamicAuthenticationTokens
	 * 
	 * @param role
	 */
	public void addRoleToAuthentication(String userId, String role) {
		List<SecurityContext> ctxList = getSecurityContextByUser(userId);
		for (SecurityContext ctx : ctxList) {
			Authentication auth = ctx.getAuthentication();
			if (auth instanceof DynamicAuthenticationToken) {
				DynamicAuthenticationToken dy = (DynamicAuthenticationToken) auth;
				synchronized (dy) {
					dy.getAuthorities().add(new SimpleGrantedAuthority(role));
				}
			}
		}
	}

	public int getSecurityContextCount() {
		return getCache().getKeysWithExpiryCheck().size();
	}

}
