package com.workstream.core.exception;

/**
 * An validation exception indicating the request is attempting to modify the
 * system to a bad state.
 * 
 * @author qinghai
 * 
 */
public class AttempBadStateException extends CoreException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1283107801755448027L;

	public AttempBadStateException() {
		super();
	}

	public AttempBadStateException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public AttempBadStateException(String message, Throwable cause) {
		super(message, cause);
	}

	public AttempBadStateException(String message) {
		super(message);
	}

	public AttempBadStateException(Throwable cause) {
		super(cause);
	}

}
