package br.com.portal_nr1.infrastructure.adapters.in.web.error;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.NoHandlerFoundException;

import br.com.portal_nr1.application.exception.GroupAlreadyExistsException;
import br.com.portal_nr1.application.exception.GroupExepiredException;
import br.com.portal_nr1.application.exception.GroupNotFoundException;
import br.com.portal_nr1.application.exception.QuestionnaireNotFoundException;
import br.com.portal_nr1.application.exception.RespondentEmailConflictException;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.RespondetResponseItem;
import br.com.portal_nr1.infrastructure.adapters.in.web.error.RepondentProvisionErrorResponse.Conflict;
import br.com.portal_nr1.infrastructure.adapters.in.web.mapper.RespondentMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex,
			HttpServletRequest request) {
		List<ApiErrorResponse.Violation> violations = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(this::toViolation)
				.toList();

		return build(HttpStatus.BAD_REQUEST, "Validation failed", request, violations);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
			ConstraintViolationException ex,
			HttpServletRequest request) {
		List<ApiErrorResponse.Violation> violations = ex.getConstraintViolations()
				.stream()
				.map(v -> new ApiErrorResponse.Violation(v.getPropertyPath().toString(), v.getMessage()))
				.toList();

		return build(HttpStatus.BAD_REQUEST, "Validation failed", request, violations);
	}

	@ExceptionHandler({
			HttpMessageNotReadableException.class,
			MissingServletRequestParameterException.class,
			MethodArgumentTypeMismatchException.class
	})
	public ResponseEntity<ApiErrorResponse> handleBadRequest(Exception ex, HttpServletRequest request) {
		return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request, List.of());
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ApiErrorResponse> handleMethodNotSupported(
			HttpRequestMethodNotSupportedException ex,
			HttpServletRequest request) {
		return build(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), request, List.of());
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNotFound(NoHandlerFoundException ex, HttpServletRequest request) {
		return build(HttpStatus.NOT_FOUND, "Resource not found", request, List.of());
	}

	@ExceptionHandler(QuestionnaireNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleQuestionnaireNotFound(
			QuestionnaireNotFoundException ex,
			HttpServletRequest request) {
		return build(HttpStatus.NOT_FOUND, ex.getMessage(), request, List.of());
	}

	@ExceptionHandler(GroupAlreadyExistsException.class)
	public ResponseEntity<ApiErrorResponse> handleGroupAlreadyExists(
			GroupAlreadyExistsException ex,
			HttpServletRequest request) {
		return build(HttpStatus.CONFLICT, ex.getMessage(), request, List.of());
	}

	@ExceptionHandler(GroupNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleGroupNotFound(
			GroupNotFoundException ex,
			HttpServletRequest request) {
		return build(HttpStatus.NOT_FOUND, ex.getMessage(), request, List.of());
	}

	@ExceptionHandler(GroupExepiredException.class)
	public ResponseEntity<ApiErrorResponse> handleGroupExpired(
			GroupExepiredException ex,
			HttpServletRequest request) {
		return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request, List.of());
	}

	@ExceptionHandler(RespondentEmailConflictException.class)
	public ResponseEntity<RepondentProvisionErrorResponse> handleRespondentEmailConflict(
			RespondentEmailConflictException ex,
			HttpServletRequest request) {

		RepondentProvisionErrorResponse body = new RepondentProvisionErrorResponse(
				ex.getCode(),
				ex.getMessage(),
				new Conflict(
						RespondentMapper.toResponse(ex.getRespondent(), RespondetResponseItem.class),
						ex.getRespondent().getGroupId(),
						ex.getRespondent().getGroupName()));

		return ResponseEntity.status(HttpStatus.CONFLICT).body(body);

	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
		return build(HttpStatus.FORBIDDEN, "Access denied", request, List.of());
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ApiErrorResponse> handleAuthentication(AuthenticationException ex,
			HttpServletRequest request) {
		return build(HttpStatus.UNAUTHORIZED, "Unauthorized", request, List.of());
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ApiErrorResponse> handleResponseStatus(
			ResponseStatusException ex,
			HttpServletRequest request) {
		HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
		if (status == null) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}

		String message = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
		return build(status, message, request, List.of());
	}

	@ExceptionHandler({ ErrorResponseException.class })
	public ResponseEntity<ApiErrorResponse> handleErrorResponseException(
			ErrorResponseException ex,
			HttpServletRequest request) {
		HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
		if (status == null) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}

		String message = ex.getMessage() != null ? ex.getMessage() : status.getReasonPhrase();
		return build(status, message, request, List.of());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", request, List.of());
	}

	private ResponseEntity<ApiErrorResponse> build(
			HttpStatus status,
			String message,
			HttpServletRequest request,
			List<ApiErrorResponse.Violation> violations) {
		ApiErrorResponse body = new ApiErrorResponse(
				Instant.now(),
				status.value(),
				status.getReasonPhrase(),
				message,
				request.getRequestURI(),
				violations);

		return ResponseEntity.status(status).body(body);
	}

	private ApiErrorResponse.Violation toViolation(FieldError error) {
		String message = error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value";
		return new ApiErrorResponse.Violation(error.getField(), message);
	}
}
