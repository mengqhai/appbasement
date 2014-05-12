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

import com.angularjsplay.exception.ScrumValidationException;
import com.appbasement.exception.ResourceNotFoundException;

@ControllerAdvice
public class ErrorHandlerAdvice {

	public ErrorHandlerAdvice() {
	}

	@ExceptionHandler(ScrumValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public RestError handleValidationError(ScrumValidationException vException) {
		RestError result = new RestError();
		result.setCode(400);

		BindingResult bResult = vException.getBindingResult();
		if (bResult != null) {
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
			result.setMessage(message.toString());
		} else {
			result.setMessage(vException.getLocalizedMessage());
		}
		return result;
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public RestError handleResourceNotFoundError() {
		RestError result = new RestError();
		result.setCode(404);
		result.setMessage("Unable to find requested resource.");
		return result;
	}

}
