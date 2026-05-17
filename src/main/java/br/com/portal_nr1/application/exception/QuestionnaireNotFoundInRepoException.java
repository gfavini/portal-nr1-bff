package br.com.portal_nr1.application.exception;

public class QuestionnaireNotFoundInRepoException extends QuestionnaireRepositoryException {

    public QuestionnaireNotFoundInRepoException(String message) {
        super(message);
    }

    public QuestionnaireNotFoundInRepoException(String message, Throwable error){
        super(message, error);
    }

}
