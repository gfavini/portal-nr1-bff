package br.com.portal_nr1.application.ports.in;

import br.com.portal_nr1.domain.model.Questionnaire;
import br.com.portal_nr1.domain.model.User;

public interface SaveQuestionnaireUseCase {
    Questionnaire save(Questionnaire request, User user);   
}