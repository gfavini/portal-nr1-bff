package br.com.portal_nr1.infrastructure.adapters.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record RespondentResponseItem(
    @Schema(description = "Identificador unico do respondente", example = "a1b2c3d4-5e6f-7g8h-9i0j-1k2l3m4n5o6p")
    String id,
    @Schema(description = "Email do respondente", example = "respondent@example.com")
    String email
) {}
