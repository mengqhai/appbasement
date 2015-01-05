package com.workstream.rest.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;

import com.workstream.rest.RestConstants;
import com.workstream.rest.security.BasicAuthenticationProvider;
import com.workstream.rest.security.NoRedirectLogoutSuccessHandler;

/**
 * See
 * http://docs.spring.io/spring-security/site/docs/3.2.x/reference/htmlsingle
 * /#multiple-httpsecurity and
 * https://github.com/spring-projects/spring-security
 * /blob/master/config/src/test
 * /groovy/org/springframework/security/config/annotation
 * /web/SampleWebSecurityConfigurerAdapterTests.groovy#L277 and
 * http://stackoverflow
 * .com/questions/18815015/creating-multiple-http-sections-in
 * -spring-security-java-config for multiple HttpSecurity.
 * 
 * @author qinghai
 * 
 */
@Configuration
@EnableWebSecurity
@EnableWebMvcSecurity
public class SecurityConfiguration {

	@Bean
	public AuthenticationProvider authenticationProvider() {
		return new BasicAuthenticationProvider();
	}

	@Bean
	public NoRedirectLogoutSuccessHandler noRedirectLogoutSuccessHandler() {
		return new NoRedirectLogoutSuccessHandler();
	}

	@Configuration
	@Order(1)
	public static class SecuredConfiguration extends
			WebSecurityConfigurerAdapter {
		@Autowired
		AuthenticationProvider authenticationProvider;

		@Autowired
		NoRedirectLogoutSuccessHandler noRedirectLogoutSuccessHandler;

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.authenticationProvider(authenticationProvider).csrf()
					.disable();
			http.authorizeRequests()
					.antMatchers(HttpMethod.POST,
							RestConstants.REST_ROOT + "/users")
					// enable user
					// reg to pass
					// the security
					.permitAll()
					.antMatchers(HttpMethod.GET,
							RestConstants.REST_ROOT + "/captcha").permitAll()
					.antMatchers(RestConstants.REST_ROOT + "/login")
					.permitAll().antMatchers("/logout").permitAll()
					.antMatchers(RestConstants.REST_ROOT + "/api-docs/**")
					.permitAll().anyRequest().authenticated().and();
			http.httpBasic();
			http.logout().logoutUrl("/logout")
					.logoutSuccessHandler(noRedirectLogoutSuccessHandler);
		}

	}

	// @Configuration
	// @Order(2)
	// public static class NonSecuredConfiguration extends
	// WebSecurityConfigurerAdapter {
	// @Autowired
	// AuthenticationProvider authenticationProvider;
	//
	// @Override
	// protected void configure(HttpSecurity http) throws Exception {
	// http.antMatcher("");
	// }
	//
	// }

}
