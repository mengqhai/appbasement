package com.angularjsplay.exception;

@SuppressWarnings("serial")
public class ScrumException extends RuntimeException {

	public ScrumException() {
	}

	public ScrumException(String message) {
		super(message);
	}

	public ScrumException(Throwable cause) {
		super(cause);
	}

	public ScrumException(String message, Throwable cause) {
		super(message, cause);
	}

	public ScrumException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
