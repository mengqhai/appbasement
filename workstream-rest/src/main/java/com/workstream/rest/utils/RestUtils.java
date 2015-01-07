package com.workstream.rest.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Base64;

import com.workstream.core.exception.BadArgumentException;

public class RestUtils {

	private final static Logger log = LoggerFactory.getLogger(RestUtils.class);

	public static String decodeUserId(String userIdBase64) {
		byte[] decoded = null;
		try {
			decoded = Base64.decode(userIdBase64.getBytes());
		} catch (Exception e) {
			log.warn("Unable to parse user id from base64: {}", userIdBase64, e);
			throw new BadArgumentException("Bad user id", e);
		}
		return new String(decoded);
	}

}
