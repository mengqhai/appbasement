package com.workstream.core.exception;

public class AuthenticationNotSetException extends CoreException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2744216694161830114L;

	public AuthenticationNotSetException() {
		super();
	}

	public AuthenticationNotSetException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public AuthenticationNotSetException(String message, Throwable cause) {
		super(message, cause);
	}

	public AuthenticationNotSetException(String message) {
		super(message);
	}

	public AuthenticationNotSetException(Throwable cause) {
		super(cause);
	}

}
