package com.workstream.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.workstream.core.exception.AuthenticationNotSetException;
import com.workstream.core.exception.BadArgumentException;
import com.workstream.rest.model.RestErrorResponse;

@ControllerAdvice
public class ErrorHandlerAdvice {

	// 404
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseBody
	public RestErrorResponse handleResourceNotFound(ResourceNotFoundException e) {
		RestErrorResponse r = new RestErrorResponse(404, e.getMessage());
		return r;
	}

	// 400
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(BadArgumentException.class)
	@ResponseBody
	public RestErrorResponse handleBadArgument(BadArgumentException e) {
		RestErrorResponse r = new RestErrorResponse(400, e.getMessage());
		return r;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(BadStateException.class)
	@ResponseBody
	public RestErrorResponse handleBadState(BadStateException e) {
		RestErrorResponse r = new RestErrorResponse(400, e.getMessage());
		return r;
	}

	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ExceptionHandler(AuthenticationNotSetException.class)
	@ResponseBody
	public RestErrorResponse handleAuthenticateNotSet(
			AuthenticationNotSetException e) {
		RestErrorResponse r = new RestErrorResponse(403,
				"You are not authenticated, please login first.");
		return r;
	}

}
