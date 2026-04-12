package br.com.portal_nr1.application.ports.out;

import java.util.ArrayList;

import br.com.portal_nr1.domain.model.Questionnaire;

public interface QuestionnaireRespositoryPort {
    ArrayList<Questionnaire> fetchAll();
    Questionnaire fetchLatest();
    Questionnaire fetch(String id);
    Questionnaire fetchByVersion(String version);
    Questionnaire saveAndUpdateLatest(Questionnaire newQuestionnaire);
}