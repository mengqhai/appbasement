package com.workstream.core.exception;

public class BeanPropertyException extends CoreException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9106676209160355198L;

	public BeanPropertyException() {
		super();
	}

	public BeanPropertyException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BeanPropertyException(String message, Throwable cause) {
		super(message, cause);
	}

	public BeanPropertyException(String message) {
		super(message);
	}

	public BeanPropertyException(Throwable cause) {
		super(cause);
	}

}
