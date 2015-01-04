package com.workstream.rest.exception;

public class BadArgumentException extends RestException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2510213091751503706L;

	public BadArgumentException() {
		super();
	}

	public BadArgumentException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BadArgumentException(String message, Throwable cause) {
		super(message, cause);
	}

	public BadArgumentException(String message) {
		super(message);
	}

	public BadArgumentException(Throwable cause) {
		super(cause);
	}
}
