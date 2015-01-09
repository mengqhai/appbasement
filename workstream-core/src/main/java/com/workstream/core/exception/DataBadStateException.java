package com.workstream.core.exception;

/**
 * Indicating that the system is already in a bad data state, somehow like an
 * assertion error. So ones it occurs there must be a bug.
 * 
 * @author qinghai
 * 
 */
public class DataBadStateException extends CoreException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2245485489322620809L;

	public DataBadStateException() {
		super();
	}

	public DataBadStateException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DataBadStateException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataBadStateException(String message) {
		super(message);
	}

	public DataBadStateException(Throwable cause) {
		super(cause);
	}

}
