package br.com.portal_nr1.application.exception;

public class GroupExepiredException extends RuntimeException {
    public GroupExepiredException(String message) {
        super(message);
    }

    public GroupExepiredException(String message, Throwable error) {
        super(message, error);
    }

}
