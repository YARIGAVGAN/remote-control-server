package com.yarossa.remotecontrol.exception;

import com.yarossa.remotecontrol.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiResponse> handleIllegalArgument(IllegalArgumentException exception) {
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(new ApiResponse(false, exception.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse> handleValidation(MethodArgumentNotValidException exception) {
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(new ApiResponse(false, "Validation failed"));
	}

	@ExceptionHandler(SecurityException.class)
	public ResponseEntity<ApiResponse> handleSecurityException(SecurityException exception) {
		return unauthorized();
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ApiResponse> handleResponseStatus(ResponseStatusException exception) {
		if (exception.getStatusCode() == HttpStatus.UNAUTHORIZED) {
			return unauthorized();
		}

		return ResponseEntity
				.status(exception.getStatusCode())
				.body(new ApiResponse(false, exception.getReason()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse> handleException(Exception exception) {
		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ApiResponse(false, exception.getMessage() == null ? "Internal server error" : exception.getMessage()));
	}

	private ResponseEntity<ApiResponse> unauthorized() {
		return ResponseEntity
				.status(HttpStatus.UNAUTHORIZED)
				.body(new ApiResponse(false, "Unauthorized"));
	}
}
