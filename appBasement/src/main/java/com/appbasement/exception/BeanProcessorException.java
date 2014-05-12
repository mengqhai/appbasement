package com.appbasement.exception;

public class BeanProcessorException extends AppBasementRTException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2505390080595635788L;

	public BeanProcessorException() {
	}

	public BeanProcessorException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BeanProcessorException(String message, Throwable cause) {
		super(message, cause);
	}

	public BeanProcessorException(String message) {
		super(message);
	}

	public BeanProcessorException(Throwable cause) {
		super(cause);
	}

}
