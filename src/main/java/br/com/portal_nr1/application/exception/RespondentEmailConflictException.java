package br.com.portal_nr1.application.exception;

import br.com.portal_nr1.domain.model.Respondent;
import lombok.Getter;

@Getter
public class RespondentEmailConflictException extends RuntimeException{
    private String code;
    private Respondent respondent;

    public RespondentEmailConflictException(String message) {
        super(message);
    }

    public RespondentEmailConflictException(String message, Throwable err) {
        super(message, err);
    }

    public RespondentEmailConflictException(String code, String message, Respondent respondent) {
        super(message);
        this.code = code;
        this.respondent = respondent;
    }

}
