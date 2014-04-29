package com.angularjsplay.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ScrumValidationException extends ScrumException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5737974196337789561L;

	private BindingResult bindingResult;

	public ScrumValidationException() {
	}

	public ScrumValidationException(String message) {
		super(message);
	}

	public ScrumValidationException(Throwable cause) {
		super(cause);
	}

	public ScrumValidationException(BindingResult bindingResult) {
		super();
		this.bindingResult = bindingResult;
	}

	public ScrumValidationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ScrumValidationException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BindingResult getBindingResult() {
		return bindingResult;
	}

	public void setBindingResult(BindingResult bindingResult) {
		this.bindingResult = bindingResult;
	}

}
