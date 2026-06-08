package br.com.portal_nr1.application.ports.in;

import br.com.portal_nr1.domain.model.Respondent;

public interface UpdateRespondentUseCase {

    Respondent update(String id, Respondent respondent);

}
