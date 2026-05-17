package br.com.portal_nr1.domain.model;

public enum GroupStatus {
    OPEN("open"),
    CLOSED("closed"),
    EXPIRED("expired");

    private final String value;

    GroupStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static GroupStatus fromString(String value) {
        for (GroupStatus status : GroupStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status: " + value);
    }
}
