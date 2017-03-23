package com.haoxin.emctest.exception;

public class LunNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6558739217192733411L;

	public LunNotFoundException() {
	}

	public LunNotFoundException(String message) {
		super(message);
	}

	public LunNotFoundException(Throwable cause) {
		super(cause);
	}

	public LunNotFoundException(String message, Throwable throwable) {
		super(message, throwable);
	}


}
