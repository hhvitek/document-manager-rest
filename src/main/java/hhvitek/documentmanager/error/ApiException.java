package hhvitek.documentmanager.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ApiException extends ResponseStatusException {
	public ApiException(String message) {
		super(HttpStatus.BAD_REQUEST, message);
	}

	public ApiException(HttpStatus status, String message) {
		super(status, message);
	}

	public ApiException(HttpStatus status, String message, Throwable throwable) {
		super(status, message, throwable);
	}
}
