package br.com.portal_nr1.application.exception;

public class GroupNotFoundException extends RuntimeException {

    public GroupNotFoundException(String message) {
        super(message);
    }

    public GroupNotFoundException(String message, Throwable error) {
        super(message, error);
    }
}
