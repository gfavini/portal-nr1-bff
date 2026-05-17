package br.com.portal_nr1.infrastructure.adapters.exception;

public class KeycloakGroupNameConflictException extends KeycloakProvisioningException {
    public KeycloakGroupNameConflictException(String message) {
        super(message);
    }

    public KeycloakGroupNameConflictException(String message, Throwable cause) {
        super(message, cause);
    }

}
