package com.workstream.rest.security.exp;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.Authentication;

public class WsSecurityExpressionHandler extends
		DefaultMethodSecurityExpressionHandler {

	protected AuthenticationTrustResolver myTrustResolver;



	@Override
	protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
			Authentication authentication, MethodInvocation invocation) {
		WsSecurityExpressionRoot root = new WsSecurityExpressionRoot(authentication);
        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(myTrustResolver);
        root.setRoleHierarchy(getRoleHierarchy());

        return root;
	}



	@Override
	public void setTrustResolver(AuthenticationTrustResolver trustResolver) {
		super.setTrustResolver(trustResolver);
		this.myTrustResolver = trustResolver;
	}

}
