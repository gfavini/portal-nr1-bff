package br.com.portal_nr1.application.exception;

public class QuestionnaireRepositoryException extends RuntimeException {

    public QuestionnaireRepositoryException(String message) {
        super(message);
    }

    public QuestionnaireRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

}
