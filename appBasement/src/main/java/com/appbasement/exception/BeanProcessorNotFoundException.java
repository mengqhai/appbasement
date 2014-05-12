package com.appbasement.exception;

public class BeanProcessorNotFoundException extends BeanProcessorException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -645785472608806106L;

	public BeanProcessorNotFoundException() {
		// TODO Auto-generated constructor stub
	}

	public BeanProcessorNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BeanProcessorNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public BeanProcessorNotFoundException(String message) {
		super(message);
	}

	public BeanProcessorNotFoundException(Throwable cause) {
		super(cause);
	}

}
