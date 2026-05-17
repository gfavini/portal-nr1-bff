package br.com.portal_nr1.infrastructure.adapters.in.web.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import br.com.portal_nr1.domain.model.InviteScope;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Escopo do ultimo convite enviado", allowableValues = {"all", "partial"})
public enum InviteScopeResponse {
    ALL("all"),
    PARTIAL("partial");

    private final String value;

    InviteScopeResponse(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static InviteScopeResponse fromDomain(InviteScope scope) {
        return switch (scope) {
            case ALL -> ALL;
            case PARTIAL -> PARTIAL;
        };
    }
}
