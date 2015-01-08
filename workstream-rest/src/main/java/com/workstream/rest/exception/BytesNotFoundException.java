package com.workstream.rest.exception;

import com.workstream.core.exception.ResourceNotFoundException;

public class BytesNotFoundException extends ResourceNotFoundException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3671153122612130611L;

	public BytesNotFoundException() {
		super();
	}

	public BytesNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BytesNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public BytesNotFoundException(String message) {
		super(message);
	}

	public BytesNotFoundException(Throwable cause) {
		super(cause);
	}

}
