package br.com.portal_nr1.infrastructure.adapters.exception;

public class UserEmailConflictException extends UserProvisioningException {
    public UserEmailConflictException(String message) {
        super(message);
    }

    public UserEmailConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
