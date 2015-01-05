package com.workstream.core.exception;

public class DataPersistException extends CoreException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9053414028689379610L;

	public DataPersistException() {
		super();
	}

	public DataPersistException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DataPersistException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataPersistException(String message) {
		super(message);
	}

	public DataPersistException(Throwable cause) {
		super(cause);
	}

}
