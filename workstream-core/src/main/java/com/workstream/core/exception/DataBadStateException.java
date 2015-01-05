package com.workstream.core.exception;

public class DataBadStateException extends CoreException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2245485489322620809L;

	public DataBadStateException() {
		super();
	}

	public DataBadStateException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DataBadStateException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataBadStateException(String message) {
		super(message);
	}

	public DataBadStateException(Throwable cause) {
		super(cause);
	}

}
