package br.com.portal_nr1.infrastructure.adapters.exception;

public class UserProvisioningException extends RuntimeException {
    public UserProvisioningException(String message) {
        super(message);
    }

    public UserProvisioningException(String message, Throwable cause) {
        super(message, cause);
    }

}
