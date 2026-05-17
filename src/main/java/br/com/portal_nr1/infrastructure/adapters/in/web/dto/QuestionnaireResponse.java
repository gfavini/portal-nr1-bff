package br.com.portal_nr1.infrastructure.adapters.in.web.dto;

import java.util.ArrayList;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta com a versao atual e o historico de versoes de questionarios")
public record QuestionnaireResponse(
    @Schema(description = "Versao atual do questionario")
    QuestionnaireResponseItem current,
    @ArraySchema(schema = @Schema(implementation = QuestionnaireResponseItem.class), arraySchema = @Schema(description = "Historico de versoes publicadas"))
    ArrayList<QuestionnaireResponseItem> versions
) {

}
