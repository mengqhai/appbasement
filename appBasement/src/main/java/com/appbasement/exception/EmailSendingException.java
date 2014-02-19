package com.appbasement.exception;

public class EmailSendingException extends AppBasementRTException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6589093675565416026L;

	public EmailSendingException() {
	}

	public EmailSendingException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public EmailSendingException(String message, Throwable cause) {
		super(message, cause);
	}

	public EmailSendingException(String message) {
		super(message);
	}

	public EmailSendingException(Throwable cause) {
		super(cause);
	}

}
