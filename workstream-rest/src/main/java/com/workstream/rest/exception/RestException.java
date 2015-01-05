package com.workstream.rest.exception;

import com.workstream.core.exception.WorkStreamException;

public class RestException extends WorkStreamException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6148681852739221632L;

	public RestException() {
		super();
	}

	public RestException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public RestException(String message, Throwable cause) {
		super(message, cause);
	}

	public RestException(String message) {
		super(message);
	}

	public RestException(Throwable cause) {
		super(cause);
	}

}
