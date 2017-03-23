package com.haoxin.emctest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.haoxin.emctest.model.RestErrorResponse;


/**
 * Handle exceptions in a unified way.
 * 
 *
 */
@ControllerAdvice
public class ExceptionHandlerAdvice {

	// 404
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(LunNotFoundException.class)
	@ResponseBody
	public RestErrorResponse handleLunNotFound(LunNotFoundException e) {
		return new RestErrorResponse(404, e.getMessage());
	}

}
