package br.com.portal_nr1.infrastructure.adapters.in.web.error;

import java.time.Instant;
import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estrutura padrao para respostas de erro da API")
public record ApiErrorResponse(
	@Schema(description = "Data e hora em que o erro ocorreu", example = "2026-04-12T12:00:00Z")
	Instant timestamp,
	@Schema(description = "Codigo HTTP retornado", example = "404")
	int status,
	@Schema(description = "Descricao curta do status HTTP", example = "Not Found")
	String error,
	@Schema(description = "Mensagem detalhando o problema", example = "Questionario da versao 99 nao encontrado")
	String message,
	@Schema(description = "Caminho da requisicao que gerou o erro", example = "/api/questionnaires/99")
	String path,
	@ArraySchema(schema = @Schema(implementation = Violation.class), arraySchema = @Schema(description = "Lista de violacoes de validacao"))
	List<Violation> violations
) {
	@Schema(description = "Detalhe de uma violacao de validacao")
	public record Violation(String field, String message) {
	}
}
