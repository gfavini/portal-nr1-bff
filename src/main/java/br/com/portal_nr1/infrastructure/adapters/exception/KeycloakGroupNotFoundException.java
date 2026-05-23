package br.com.portal_nr1.infrastructure.adapters.exception;

public class KeycloakGroupNotFoundException extends KeycloakProvisioningException {
    public KeycloakGroupNotFoundException(String message) {
        super(message);
    }

    public KeycloakGroupNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
