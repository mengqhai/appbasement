package com.workstream.rest.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BeanValidationException extends RestException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5737974196337789561L;

	private BindingResult bindingResult;

	public BeanValidationException() {
	}

	public BeanValidationException(String message) {
		super(message);
	}

	public BeanValidationException(Throwable cause) {
		super(cause);
	}

	public BeanValidationException(BindingResult bindingResult) {
		super();
		this.bindingResult = bindingResult;
	}

	public BeanValidationException(String message, Throwable cause) {
		super(message, cause);
	}

	public BeanValidationException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BindingResult getBindingResult() {
		return bindingResult;
	}

	public void setBindingResult(BindingResult bindingResult) {
		this.bindingResult = bindingResult;
	}

	public String getBindingErrorMessage() {
		List<ObjectError> allErrors = bindingResult.getAllErrors();
		StringBuilder message = new StringBuilder();
		for (int i = 0; i < allErrors.size(); i++) {
			ObjectError error = allErrors.get(i);
			if (error instanceof FieldError) {
				message.append("Field ");
				message.append(((FieldError) error).getField()).append(
						" on object ");
				message.append(error.getObjectName());
			} else {
				message.append(error.getObjectName());
			}
			message.append(":").append(error.getDefaultMessage());
			if (i != allErrors.size() - 1) {
				message.append("\n");
			}
		}
		return message.toString();
	}

}
