package com.workstream.rest.exception;

public class NotAuthorizedException extends RestException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3813508076282790530L;

	public NotAuthorizedException() {
		super();
	}

	public NotAuthorizedException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NotAuthorizedException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotAuthorizedException(String message) {
		super(message);
	}

	public NotAuthorizedException(Throwable cause) {
		super(cause);
	}

}
