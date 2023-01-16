package hhvitek.documentmanager.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiError {
	private int status;
	private String message;

	public ApiError(int status, String message) {
		this.status = status;
		this.message = message;
	}

	public String toJson() throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(this);
	}
}
