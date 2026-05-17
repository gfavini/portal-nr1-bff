package br.com.portal_nr1.infrastructure.adapters.in.web.dto;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

public record GroupRequestItem(
    @Schema(description = "Nome do grupo", example = "Grupo A")
    String name,
    @Schema(description = "Identificador do questionario atribuido", example = "c1d2e3f4-5g6h-7i8j-9k0l-1m2n3o4p5q6r")
    String assignedQuestionnaireId,
    @Schema(description = "Versao do questionario atribuido", example = "1")
    Integer assignedQuestionnaireVersion,
    @Schema(description = "Data e hora de expiracao do grupo", example = "2023-12-31T23:59:59Z")
    @NotNull(message = "expiresAt is required")
    @Future(message = "expiresAt must be in the future")
    Instant expiresAt,
    @Schema(description = "Status atual do grupo", example = "open")
    String status
) {
}
