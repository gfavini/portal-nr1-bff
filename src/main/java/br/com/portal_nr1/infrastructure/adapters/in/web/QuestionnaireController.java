package br.com.portal_nr1.infrastructure.adapters.in.web;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.portal_nr1.application.ports.in.FetchQuestionnaireByVersionUseCase;
import br.com.portal_nr1.application.ports.in.FetchQuestionnairesUseCase;
import br.com.portal_nr1.application.ports.in.SaveQuestionnaireUseCase;
import br.com.portal_nr1.domain.model.Questionnaire;
import br.com.portal_nr1.domain.model.Questionnaires;
import br.com.portal_nr1.domain.model.User;
import br.com.portal_nr1.infrastructure.adapters.in.web.api.QuestionnaireApi;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.QuestionnaireRequestItem;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.QuestionnaireResponse;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.QuestionnaireResponseItem;
import br.com.portal_nr1.infrastructure.adapters.in.web.mapper.AuthenticatedUserMapper;
import br.com.portal_nr1.infrastructure.adapters.in.web.mapper.QuestionnaireMapper;
import br.com.portal_nr1.infrastructure.security.AuthenticatedUser;
import br.com.portal_nr1.infrastructure.security.AuthenticatedUserResolver;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/questionnaires")
@RequiredArgsConstructor
public class QuestionnaireController implements QuestionnaireApi {

    private final FetchQuestionnairesUseCase fetchQuestionnaireUseCase;
    private final FetchQuestionnaireByVersionUseCase fetchQuestionnaireByVersionUseCase;
    private final SaveQuestionnaireUseCase saveQuestionnaireUseCase;
    private final AuthenticatedUserResolver authenticatedUserResolver;

    // TODO: Autorização: Somente usuários com ADMIN
    @GetMapping("/")
    @Override
    public QuestionnaireResponse listQuestionnaires() {
        Questionnaires questionnaires = fetchQuestionnaireUseCase.fetchQuestionnaires();
        return QuestionnaireMapper.toResponse(questionnaires);

    }
    // TODO: Autorização: Com usuários com ADMIN e RESPONDENT
    @GetMapping("/{version}")
    @Override
    public QuestionnaireResponseItem getQuestionnaireByVersion(@PathVariable Integer version) {
        Questionnaire questionnaire = fetchQuestionnaireByVersionUseCase.fetchQuestionnaire(version);
        return QuestionnaireMapper.toResponse(questionnaire);
    }


    // TODO: Autorização: Somente usuários com ADMIN
    @PostMapping("/versions")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public QuestionnaireResponseItem createQuestionnaireVersion(@RequestBody QuestionnaireRequestItem request,
            Principal principal,
            Authentication authentication) {
        
        AuthenticatedUser authenticatedUser = authenticatedUserResolver.resolve(principal, authentication);

        User user = AuthenticatedUserMapper.toDomain(authenticatedUser);
        Questionnaire input = QuestionnaireMapper.toDomain(request);
        
        Questionnaire questionnaire = saveQuestionnaireUseCase.save(input, user);
        return QuestionnaireMapper.toResponse(questionnaire);

    }

}
