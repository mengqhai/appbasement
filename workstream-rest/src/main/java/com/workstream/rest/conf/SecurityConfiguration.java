package com.workstream.rest.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;

import com.workstream.rest.RestConstants;
import com.workstream.rest.security.BasicAuthenticationProvider;
import com.workstream.rest.security.CORS401UnauthorizedEntryPoint;
import com.workstream.rest.security.NoRedirectLogoutSuccessHandler;
import com.workstream.rest.security.RestTokenSecurityContextRepository;

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
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

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

		@Bean
		public CORS401UnauthorizedEntryPoint cors403ForbiddenEntryPoint() {
			return new CORS401UnauthorizedEntryPoint();
		}

		@Bean
		public RestTokenSecurityContextRepository restTokenSecurityContextRepository() {
			return new RestTokenSecurityContextRepository();
		}

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
					.permitAll().antMatchers("/swagger/**")
					.permitAll()
					.anyRequest()
					.authenticated()
					.and()
					// This enables ConcurrentSessionFilter that will force the
					// HttpSevletSession to invalidate if
					// SessionInformation.expireNow()
					// is somehow invoked.
					.sessionManagement().maximumSessions(5)
					.sessionRegistry(sessionRegistry())
					// redirect to a URL that gives a 401 code
					.expiredUrl(RestConstants.REST_ROOT + "/login/_count");
			http.httpBasic().authenticationEntryPoint(
					cors403ForbiddenEntryPoint());
			http.logout().logoutUrl("/logout")
					.logoutSuccessHandler(noRedirectLogoutSuccessHandler);
			http.securityContext().securityContextRepository(
					restTokenSecurityContextRepository());
		}

		/**
		 * Must create the session registry bean in the dispather context,
		 * otherwise it won't receive the session events.
		 * 
		 * @return
		 */
		@Bean
		public SessionRegistry sessionRegistry() {
			return new SessionRegistryImpl();
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

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth)
			throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}

}
