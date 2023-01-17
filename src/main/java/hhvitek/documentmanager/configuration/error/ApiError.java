package hhvitek.documentmanager.configuration.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;

@Getter
public class ApiError {
	private final int status;
	private final String message;

	public ApiError(int status, String message) {
		this.status = status;
		this.message = message;
	}

	public String toJson() throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(this);
	}
}
