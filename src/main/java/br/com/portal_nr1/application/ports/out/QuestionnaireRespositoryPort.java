package br.com.portal_nr1.application.ports.out;

import java.util.ArrayList;

import br.com.portal_nr1.application.exception.QuestionnaireRepositoryException;
import br.com.portal_nr1.domain.model.Questionnaire;


public interface QuestionnaireRespositoryPort {
    ArrayList<Questionnaire> fetchAll();
    Questionnaire fetchLatest() throws QuestionnaireRepositoryException;
    Questionnaire fetch(String id) throws QuestionnaireRepositoryException;
    Questionnaire saveAndUpdateLatest(Questionnaire newQuestionnaire);
    Questionnaire fetchByVersion(Integer version) throws QuestionnaireRepositoryException;
}