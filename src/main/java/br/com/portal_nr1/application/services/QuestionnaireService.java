package br.com.portal_nr1.application.services;

import java.time.Instant;
import java.util.ArrayList;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.portal_nr1.application.ports.in.FetchQuestionnaireByVersionUseCase;
import br.com.portal_nr1.application.ports.in.FetchQuestionnairesUseCase;
import br.com.portal_nr1.application.ports.in.SaveQuestionnaireUseCase;
import br.com.portal_nr1.application.ports.out.QuestionnaireRespositoryPort;
import br.com.portal_nr1.domain.model.Questionnaire;
import br.com.portal_nr1.domain.model.Questionnaires;
import br.com.portal_nr1.domain.model.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionnaireService implements FetchQuestionnairesUseCase,
    FetchQuestionnaireByVersionUseCase,
    SaveQuestionnaireUseCase 
{

    private final QuestionnaireRespositoryPort questionnaireRepository;

    @Override
    public Questionnaires fetchQuestionnaires() {

        Questionnaire current = questionnaireRepository.fetchLatest();
        ArrayList<Questionnaire> versions = questionnaireRepository.fetchAll();

        Questionnaires questionnaires = Questionnaires.builder()
            .current(current)
            .versions(versions)
            .build();

        return questionnaires;
    }

    @Override
    public Questionnaire fetchQuestionnaire(String version) {
        Questionnaire questionnaire = questionnaireRepository.fetchByVersion(version);
        return questionnaire;
    }

    @Transactional
    @Override
    public Questionnaire save(Questionnaire request, User authenticatedUser) {
        Questionnaire newQuestionnaire = new Questionnaire(
            null,
            null,
            Instant.now().toEpochMilli(),
            authenticatedUser.username(),
            request.getTitle(),
            request.getDescription(),
            request.getQuestions()
        );

        return questionnaireRepository.saveAndUpdateLatest(newQuestionnaire);
    }

}
