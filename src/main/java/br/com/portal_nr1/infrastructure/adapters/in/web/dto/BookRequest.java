package br.com.portal_nr1.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record BookRequest(
	@NotBlank(message = "title is required") String title,
	@NotBlank(message = "author is required") String author
) {
}