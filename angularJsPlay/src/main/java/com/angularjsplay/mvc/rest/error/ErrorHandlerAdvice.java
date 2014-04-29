package com.angularjsplay.mvc.rest.error;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.angularjsplay.exception.ScrumResourceNotFoundException;
import com.angularjsplay.exception.ScrumValidationException;

@ControllerAdvice
public class ErrorHandlerAdvice {

	public ErrorHandlerAdvice() {
	}

	@ExceptionHandler(ScrumValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public RestError handleValidationError(ScrumValidationException vException) {
		BindingResult bResult = vException.getBindingResult();
		List<ObjectError> allErrors = bResult.getAllErrors();
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

		RestError result = new RestError();
		result.setCode(400);
		result.setMessage(message.toString());
		return result;
	}

	@ExceptionHandler(ScrumResourceNotFoundException.class)
	@ResponseBody
	public RestError handleResourceNotFoundError() {
		RestError result = new RestError();
		result.setCode(404);
		result.setMessage("Unable to find requested resource.");
		return result;
	}

}
