package com.workstream.core.exception;

public class WorkStreamException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7878101457817659290L;

	public WorkStreamException() {
		super();
	}

	public WorkStreamException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public WorkStreamException(String message, Throwable cause) {
		super(message, cause);
	}

	public WorkStreamException(String message) {
		super(message);
	}

	public WorkStreamException(Throwable cause) {
		super(cause);
	}

}
