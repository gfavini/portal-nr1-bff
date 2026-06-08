package br.com.portal_nr1.application.exception;

public class RespondentNotFoundException extends RuntimeException{
    public RespondentNotFoundException(String message) {
        super(message);
    }

    public RespondentNotFoundException(String message, Throwable err) {
        super(message, err);
    }
}
