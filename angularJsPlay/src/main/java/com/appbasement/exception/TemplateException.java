package com.appbasement.exception;

public class TemplateException extends AppBasementRTException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8833878333803941778L;

	public TemplateException() {
	}

	public TemplateException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TemplateException(String message, Throwable cause) {
		super(message, cause);
	}

	public TemplateException(String message) {
		super(message);
	}

	public TemplateException(Throwable cause) {
		super(cause);
	}

}
