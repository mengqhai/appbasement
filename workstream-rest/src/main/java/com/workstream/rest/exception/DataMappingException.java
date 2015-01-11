package com.workstream.rest.exception;

public class DataMappingException extends RestException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4725029263109188972L;

	public DataMappingException() {
		super();
	}

	public DataMappingException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DataMappingException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataMappingException(String message) {
		super(message);
	}

	public DataMappingException(Throwable cause) {
		super(cause);
	}
	
	

}
