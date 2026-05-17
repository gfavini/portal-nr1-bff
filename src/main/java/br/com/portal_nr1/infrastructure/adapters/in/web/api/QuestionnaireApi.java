package br.com.portal_nr1.infrastructure.adapters.in.web.api;

import java.security.Principal;

import org.springframework.security.core.Authentication;

import br.com.portal_nr1.infrastructure.adapters.in.web.dto.QuestionnaireRequestItem;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.QuestionnaireResponse;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.QuestionnaireResponseItem;
import br.com.portal_nr1.infrastructure.adapters.in.web.error.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Questionarios", description = "Operacoes de consulta e publicacao de questionarios")
@SecurityRequirement(name = "oauth2")
public interface QuestionnaireApi {

	String LIST_RESPONSE_EXAMPLE = """
		{
		  "current": {
		    "id": "b7f8d2b0-7d9a-4d49-9f64-102da8a9f101",
		    "version": 3,
		    "publishedAt": "2026-04-12T12:00:00Z",
		    "createdBy": "admin.portal",
		    "title": "Questionario NR1 2026",
		    "description": "Versao atual do questionario regulamentar.",
		    "questions": [
		      {
		        "id": "q-01",
		        "affirmation": "A empresa possui programa formal de capacitacao?",
		        "pillar": "Governanca",
		        "subPillar": "Treinamento",
		        "required": true
		      }
		    ]
		  },
		  "versions": [
		    {
		      "id": "b7f8d2b0-7d9a-4d49-9f64-102da8a9f101",
		      "version": 3,
		      "publishedAt": "2026-04-12T12:00:00Z",
		      "createdBy": "admin.portal",
		      "title": "Questionario NR1 2026",
		      "description": "Versao atual do questionario regulamentar.",
		      "questions": [
		        {
		          "id": "q-01",
		          "affirmation": "A empresa possui programa formal de capacitacao?",
		          "pillar": "Governanca",
		          "subPillar": "Treinamento",
		          "required": true
		        }
		      ]
		    }
		  ]
		}
		""";

	String ITEM_RESPONSE_EXAMPLE = """
		{
		  "id": "b7f8d2b0-7d9a-4d49-9f64-102da8a9f101",
		  "version": 3,
		  "publishedAt": "2026-04-12T12:00:00Z",
		  "createdBy": "admin.portal",
		  "title": "Questionario NR1 2026",
		  "description": "Versao atual do questionario regulamentar.",
		  "questions": [
		    {
		      "id": "q-01",
		      "affirmation": "A empresa possui programa formal de capacitacao?",
		      "pillar": "Governanca",
		      "subPillar": "Treinamento",
		      "required": true
		    }
		  ]
		}
		""";

	String CREATE_REQUEST_EXAMPLE = """
		{
		  "title": "Questionario NR1 2026",
		  "description": "Versao atualizada para auditoria anual.",
		  "questions": [
		    {
		      "id": "q-01",
		      "affirmation": "A empresa possui programa formal de capacitacao?",
		      "pillar": "Governanca",
		      "subPillar": "Treinamento",
		      "required": true
		    }
		  ]
		}
		""";

	String ERROR_RESPONSE_EXAMPLE = """
		{
		  "timestamp": "2026-04-12T12:00:00Z",
		  "status": 404,
		  "error": "Not Found",
		  "message": "Questionario da versao 99 nao encontrado",
		  "path": "/api/questionnaires/99",
		  "violations": []
		}
		""";

	@Operation(
			summary = "Listar questionarios",
			description = "Retorna a versao atual do questionario e o historico de versoes publicadas."
	)
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "Questionarios retornados com sucesso",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = QuestionnaireResponse.class),
							examples = @ExampleObject(name = "Lista de questionarios", value = LIST_RESPONSE_EXAMPLE)
					)
			),
			@ApiResponse(
					responseCode = "401",
					description = "Usuario nao autenticado",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiErrorResponse.class),
							examples = @ExampleObject(name = "Nao autenticado", value = ERROR_RESPONSE_EXAMPLE)
					)
			),
			@ApiResponse(
					responseCode = "403",
					description = "Usuario autenticado sem permissao",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
			),
			@ApiResponse(
					responseCode = "500",
					description = "Erro interno do servidor",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
			)
	})
	QuestionnaireResponse listQuestionnaires();

	@Operation(
			summary = "Buscar questionario por versao",
			description = "Retorna uma versao especifica do questionario pelo numero da versao."
	)
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "Questionario encontrado com sucesso",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = QuestionnaireResponseItem.class),
							examples = @ExampleObject(name = "Questionario por versao", value = ITEM_RESPONSE_EXAMPLE)
					)
			),
			@ApiResponse(
					responseCode = "401",
					description = "Usuario nao autenticado",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
			),
			@ApiResponse(
					responseCode = "403",
					description = "Usuario autenticado sem permissao",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
			),
			@ApiResponse(
					responseCode = "404",
					description = "Questionario nao encontrado para a versao informada",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiErrorResponse.class),
							examples = @ExampleObject(name = "Versao inexistente", value = ERROR_RESPONSE_EXAMPLE)
					)
			),
			@ApiResponse(
					responseCode = "500",
					description = "Erro interno do servidor",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
			)
	})
	QuestionnaireResponseItem getQuestionnaireByVersion(
			@Parameter(description = "Numero da versao do questionario", example = "3", required = true) Integer version
	);

	@Operation(
			summary = "Publicar nova versao do questionario",
			description = "Cria uma nova versao do questionario com base nos dados enviados pelo usuario autenticado."
	)
	@RequestBody(
			required = true,
			description = "Dados da nova versao do questionario.",
			content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = QuestionnaireRequestItem.class),
					examples = @ExampleObject(name = "Nova versao", value = CREATE_REQUEST_EXAMPLE)
			)
	)
	@ApiResponses({
			@ApiResponse(
					responseCode = "201",
					description = "Questionario criado com sucesso",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = QuestionnaireResponseItem.class),
							examples = @ExampleObject(name = "Questionario criado", value = ITEM_RESPONSE_EXAMPLE)
					)
			),
			@ApiResponse(
					responseCode = "400",
					description = "Payload invalido ou mal formatado",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
			),
			@ApiResponse(
					responseCode = "401",
					description = "Usuario nao autenticado",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
			),
			@ApiResponse(
					responseCode = "403",
					description = "Usuario autenticado sem permissao",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
			),
			@ApiResponse(
					responseCode = "500",
					description = "Erro interno do servidor",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
			)
	})
	QuestionnaireResponseItem createQuestionnaireVersion(
			@Parameter(hidden = true) QuestionnaireRequestItem request,
			@Parameter(hidden = true) Principal principal,
			@Parameter(hidden = true) Authentication authentication
	);
}