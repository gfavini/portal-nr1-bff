package br.com.portal_nr1.infrastructure.adapters.in.web.dto;

import java.time.Instant;
import java.util.ArrayList;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representa uma versao publicada do questionario")
public record QuestionnaireResponseItem(
    @Schema(description = "Identificador unico da versao", example = "b7f8d2b0-7d9a-4d49-9f64-102da8a9f101")
    String id,
    @Schema(description = "Numero sequencial da versao publicada", example = "3")
    Integer version,
    @Schema(description = "Data e hora de publicacao da versao", example = "2026-04-12T12:00:00Z")
    Instant publishedAt,
    @Schema(description = "Usuario responsavel pela publicacao", example = "admin.portal")
    String createdBy,
    @Schema(description = "Titulo da versao publicada", example = "Questionario NR1 2026")
    String title,
    @Schema(description = "Descricao resumida da versao", example = "Versao atual do questionario regulamentar.")
    String description,
    @ArraySchema(schema = @Schema(implementation = QuestionResponseItem.class), arraySchema = @Schema(description = "Perguntas disponiveis na versao"))
    ArrayList<QuestionResponseItem> questions

) {

}
