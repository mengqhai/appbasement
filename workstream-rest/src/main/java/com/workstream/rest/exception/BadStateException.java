package com.workstream.rest.exception;

public class BadStateException extends RestException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1283107801755448027L;

	public BadStateException() {
		super();
	}

	public BadStateException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BadStateException(String message, Throwable cause) {
		super(message, cause);
	}

	public BadStateException(String message) {
		super(message);
	}

	public BadStateException(Throwable cause) {
		super(cause);
	}

}
