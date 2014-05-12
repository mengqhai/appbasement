package com.appbasement.exception;

public class ResourceNotFoundException extends AppBasementRTException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8243929957756709858L;

	public ResourceNotFoundException() {
	}

	public ResourceNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ResourceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ResourceNotFoundException(String message) {
		super(message);
	}

	public ResourceNotFoundException(Throwable cause) {
		super(cause);
	}

}
