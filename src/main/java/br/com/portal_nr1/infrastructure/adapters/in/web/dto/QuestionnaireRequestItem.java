package br.com.portal_nr1.infrastructure.adapters.in.web.dto;

import java.util.ArrayList;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload utilizado para criar uma nova versao de questionario")
public record QuestionnaireRequestItem(
    @Schema(description = "Titulo da nova versao do questionario", example = "Questionario NR1 2026")
    String title,
    @Schema(description = "Descricao resumida da versao publicada", example = "Versao atualizada para auditoria anual.")
    String description,
    @ArraySchema(schema = @Schema(implementation = QuestionResponseItem.class), arraySchema = @Schema(description = "Perguntas publicadas nesta versao"))
    ArrayList<QuestionResponseItem> questions
) {
}