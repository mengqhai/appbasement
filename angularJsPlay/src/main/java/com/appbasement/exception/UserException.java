package com.appbasement.exception;

public class UserException extends AppBasementRTException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1831574431704953219L;

	public UserException() {
	}

	public UserException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UserException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserException(String message) {
		super(message);
	}

	public UserException(Throwable cause) {
		super(cause);
	}

}
