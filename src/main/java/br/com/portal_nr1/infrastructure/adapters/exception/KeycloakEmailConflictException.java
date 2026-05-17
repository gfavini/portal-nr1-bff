package br.com.portal_nr1.infrastructure.adapters.exception;

public class KeycloakEmailConflictException extends KeycloakProvisioningException {
    public KeycloakEmailConflictException(String message) {
        super(message);
    }

    public KeycloakEmailConflictException(String message, Throwable cause) {
        super(message, cause);
    }

}
