package br.com.portal_nr1.infrastructure.adapters.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record QuestionResponseItem(
    @Schema(description = "Identificador da pergunta", example = "q-01")
    String id,
    @Schema(description = "Enunciado apresentado ao respondente", example = "A empresa possui programa formal de capacitacao?")
    String affirmation,
    @Schema(description = "Pilar principal da pergunta", example = "Governanca")
    String pillar,
    @Schema(description = "Subpilar associado", example = "Treinamento")
    String subPillar,
    @Schema(description = "Indica se a resposta da pergunta e obrigatoria", example = "true")
    Boolean required
) {}
