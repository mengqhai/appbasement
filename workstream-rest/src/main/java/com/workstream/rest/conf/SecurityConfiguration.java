package com.workstream.rest.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;

import com.workstream.rest.security.BasicAuthenticationProvider;

@Configuration
@EnableWebSecurity
@EnableWebMvcSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Bean
	public AuthenticationProvider authenticationProvider() {
		return new BasicAuthenticationProvider();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authenticationProvider(authenticationProvider()).csrf().disable();
		http.authorizeRequests().anyRequest().authenticated().and();
		http.httpBasic();
	}

}
