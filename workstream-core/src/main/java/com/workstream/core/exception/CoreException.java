package com.workstream.core.exception;

public class CoreException extends WorkStreamException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1666437071600018856L;

	public CoreException() {
		super();
	}

	public CoreException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CoreException(String message, Throwable cause) {
		super(message, cause);
	}

	public CoreException(String message) {
		super(message);
	}

	public CoreException(Throwable cause) {
		super(cause);
	}

}
