package com.angularjsplay.e2e.util;

import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.RestTemplate;

public class BasicAuthRestTemplate extends RestTemplate {

	protected String username;
	protected String password;

	public BasicAuthRestTemplate() {
	}

	public BasicAuthRestTemplate(ClientHttpRequestFactory requestFactory) {
		super(requestFactory);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	protected String encodeCreds() {
		if (username == null || password == null) {
			return null;
		} else {
			String plainCreds = username + ":" + password;
			byte[] plainCredsBytes = plainCreds.getBytes();
			byte[] base64CredsBytes = Base64.encode(plainCredsBytes);
			String base64Creds = new String(base64CredsBytes);
			return base64Creds;
		}
	}

	@Override
	protected ClientHttpRequest createRequest(URI url, HttpMethod method)
			throws IOException {
		ClientHttpRequest request = super.createRequest(url, method);
		String base64Creds = encodeCreds();
		if (base64Creds != null) {
			request.getHeaders().add("Authorization", "Basic " + base64Creds);
		}
		return request;
	}

}
