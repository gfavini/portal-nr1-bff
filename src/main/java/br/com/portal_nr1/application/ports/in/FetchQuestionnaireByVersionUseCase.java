package br.com.portal_nr1.application.ports.in;

import br.com.portal_nr1.domain.model.Questionnaire;

public interface FetchQuestionnaireByVersionUseCase {
    Questionnaire  fetchQuestionnaire(String version);
}
