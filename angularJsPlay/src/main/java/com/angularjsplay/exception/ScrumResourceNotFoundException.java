package com.angularjsplay.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ScrumResourceNotFoundException extends ScrumException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6716464288031984689L;

	public ScrumResourceNotFoundException() {
	}

	public ScrumResourceNotFoundException(String message) {
		super(message);
	}

	public ScrumResourceNotFoundException(Throwable cause) {
		super(cause);
	}

	public ScrumResourceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ScrumResourceNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
