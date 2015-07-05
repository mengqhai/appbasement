package com.workstream.rest.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.activiti.engine.identity.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.workstream.core.model.Organization;
import com.workstream.core.model.UserX;
import com.workstream.core.service.OrganizationService;
import com.workstream.core.service.UserService;

public class BasicAuthenticationProvider implements AuthenticationProvider {

	private static final Logger log = LoggerFactory
			.getLogger(BasicAuthenticationProvider.class);

	@Autowired
	private UserService uSer;

	@Autowired
	private OrganizationService orgSer;

	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		String userId = authentication.getName();
		String password = authentication.getCredentials().toString();

		boolean passed = uSer.checkPassword(userId, password);
		if (passed) {
			Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();

			// for better performance of WsSecurityExpressionRoot.isAuthInOrg()
			UserX userX = uSer.getUserX(userId);
			Collection<Organization> myOrgs = orgSer.filterOrg(userX);
			for (Organization org : myOrgs) {
				grantedAuthorities.add(new SimpleGrantedAuthority(String
						.valueOf(org.getId())));
			}

			List<Group> groups = uSer.filterGroupByUser(userId);

			for (Group group : groups) {
				grantedAuthorities
						.add(new SimpleGrantedAuthority(group.getId()));
			}

			uSer.login(userId);
			log.info("User logged in: {}", userId);
			if (log.isTraceEnabled()) {
				log.trace("UserX: {}", uSer.getUserX(userId));
			}
			// return new UsernamePasswordAuthenticationToken(userId, password,
			// grantedAuthorities);
			return new DynamicAuthenticationToken(userId, password,
					grantedAuthorities);
		} else {
			throw new BadCredentialsException(
					"Authentication failed for this username and password");
		}
	}

	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
