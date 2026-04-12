package br.com.portal_nr1.infrastructure.adapters.in.web.error;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(
	Instant timestamp,
	int status,
	String error,
	String message,
	String path,
	List<Violation> violations
) {
	public record Violation(String field, String message) {
	}
}
