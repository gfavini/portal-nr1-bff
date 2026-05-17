package br.com.portal_nr1.infrastructure.adapters.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record RespondentRequestItem(
    @Schema(description = "Email do respondente", example = "respondent@example.com")
    String email
) {


}
