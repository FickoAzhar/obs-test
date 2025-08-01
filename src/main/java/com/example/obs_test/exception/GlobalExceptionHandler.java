package com.example.obs_test.exception;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex,
			HttpHeaders headers,
			HttpStatusCode status,
			WebRequest request) {

		List<String> errors = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.toList();

		ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
		problemDetail.setTitle("Validation error");
		problemDetail.setProperty("errors", errors);

		return ResponseEntity.badRequest().body(problemDetail);
	}

	@ExceptionHandler(DataNotFoundException.class)
	public ProblemDetail handleItemNotFound(DataNotFoundException ex) {
		ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
		problemDetail.setTitle("Item Not Found");
		problemDetail.setDetail(ex.getMessage());
		problemDetail.setProperty("timestamp", LocalDateTime.now());
		return problemDetail;
	}
}