package br.com.portal_nr1.infrastructure.adapters.in.web.dto;

import com.fasterxml.jackson.annotation.JsonValue;

import br.com.portal_nr1.domain.model.Gender;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Status atual do grupo", allowableValues = { "masculino", "feminino", "nao_binario" })
public enum RespondentGender {
    MASCULINO("masculino"),
    FEMININO("feminino"),
    NAO_BINARIO("nao_binario");

    private final String value;

    RespondentGender(String value) {
        this.value = value.toLowerCase();
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static RespondentGender fromDomain(Gender gender) {
        return switch (gender) {
            case MASCULINO -> RespondentGender.MASCULINO;
            case FEMININO -> RespondentGender.FEMININO;
            case NAO_BINARIO -> RespondentGender.NAO_BINARIO;
        };
    }

    public static Gender toDomain(RespondentGender gender) {
        return switch (gender) {
            case MASCULINO -> Gender.MASCULINO;
            case FEMININO -> Gender.FEMININO;
            case NAO_BINARIO -> Gender.NAO_BINARIO;
        };

    }

}
