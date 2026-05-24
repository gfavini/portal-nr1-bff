package br.com.portal_nr1.domain.model;

public enum Gender {

    MASCULINO("masculino"),
    FEMININO("feminino"),
    NAO_BINARIO("nao_binario");

    private final String value;

    Gender(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Gender fromString(String value) {
        for (Gender status : Gender.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status: " + value);
    }
    
}
