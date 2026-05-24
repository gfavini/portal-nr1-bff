package br.com.portal_nr1.infrastructure.adapters.in.web.dto;

import java.time.Instant;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;



public record GroupResponseItem(
    @Schema(description = "Identificador unico da versao", example = "b7f8d2b0-7d9a-4d49-9f64-102da8a9f101")
    String id,
    @Schema(description = "Nome do grupo", example = "Grupo A")
    String name,
    @Schema(description = "Quantidade de respondentes associados ao grupo", example = "10")
    Integer respondentsCount,
    @ArraySchema(schema = @Schema(implementation = String.class), arraySchema = @Schema(description = "Set de departamentos associados ao grupo"))
    Set<String> departments,
    @Schema(description = "Identificador do questionario atribuido", example = "c1d2e3f4-5g6h-7i8j-9k0l-1m2n3o4p5q6r")
    String assignedQuestionnaireId,
    @Schema(description = "Versao do questionario atribuido", example = "1")
    Integer assignedQuestionnaireVersion,
    GroupStatusResponse status,
    @Schema(description = "Data e hora de expiracao do grupo", example = "2023-12-31T23:59:59Z")
    Instant expiresAt,
    @Schema(description = "Data e hora do ultimo convite enviado", example = "2023-01-01T12:00:00Z")
    Instant lastInviteSentAt,
    InviteScopeResponse lastInviteScope
) {}
