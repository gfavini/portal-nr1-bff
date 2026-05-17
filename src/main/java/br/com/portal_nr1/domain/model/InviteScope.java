package br.com.portal_nr1.domain.model;

public enum InviteScope {
    ALL("all"),
    PARTIAL("partial");

    private final String value;

    InviteScope(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }

    public static InviteScope fromString(String value) {
        for (InviteScope scope : InviteScope.values()) {
            if (scope.value.equalsIgnoreCase(value)) {
                return scope;
            }
        }
        throw new IllegalArgumentException("Invalid scope: " + value);
    }

}