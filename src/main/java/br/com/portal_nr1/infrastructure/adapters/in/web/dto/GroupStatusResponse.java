package br.com.portal_nr1.infrastructure.adapters.in.web.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import br.com.portal_nr1.domain.model.GroupStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Status atual do grupo", allowableValues = {"open", "closed", "expired"})
public enum GroupStatusResponse {
    OPEN("open"),
    CLOSED("closed"),
    EXPIRED("expired");

    private final String value;

    GroupStatusResponse(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static GroupStatusResponse fromDomain(GroupStatus status) {
        return switch (status) {
            case OPEN -> OPEN;
            case CLOSED -> CLOSED;
            case EXPIRED -> EXPIRED;
        };
    }
}
