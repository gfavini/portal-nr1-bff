package br.com.portal_nr1.application.ports.in;

import br.com.portal_nr1.domain.model.Respondent;

public interface CreateRespondentUseCase {
    Respondent create (Respondent respondent);
    
}
