package com.appbasement.exception;

public class AppBasementRTException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9024666282636937545L;

	public AppBasementRTException() {
		super();
	}

	public AppBasementRTException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public AppBasementRTException(String message, Throwable cause) {
		super(message, cause);
	}

	public AppBasementRTException(String message) {
		super(message);
	}

	public AppBasementRTException(Throwable cause) {
		super(cause);
	}

}
