package br.com.portal_nr1.application.exception;

public class GroupClosedException extends RuntimeException{
    public GroupClosedException(String message) {
        super(message);
    }

    public GroupClosedException(String message, Throwable error) {
        super(message, error);
    }
}
