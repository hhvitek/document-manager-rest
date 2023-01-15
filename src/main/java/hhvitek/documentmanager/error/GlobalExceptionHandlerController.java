package hhvitek.documentmanager.error;


import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import hhvitek.documentmanager.ApiException;
import lombok.Getter;

@RestControllerAdvice
public class GlobalExceptionHandlerController {

	@ExceptionHandler(
		{
			ApiException.class, NoSuchElementException.class
		}
	)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiError error(HttpServletRequest request, RuntimeException ex) {
		return new ApiError(ex.toString());
	}

	@Getter
	private static class ApiError {
		private final String errorMessage;

		private ApiError(String errorMessage) {
			this.errorMessage = errorMessage;
		}
	}
}
