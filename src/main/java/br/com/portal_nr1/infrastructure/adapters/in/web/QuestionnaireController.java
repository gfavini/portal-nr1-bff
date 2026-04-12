package br.com.portal_nr1.infrastructure.adapters.in.web;

import java.security.Principal;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.portal_nr1.application.ports.in.FetchQuestionnaireByVersionUseCase;
import br.com.portal_nr1.application.ports.in.FetchQuestionnairesUseCase;
import br.com.portal_nr1.application.ports.in.SaveQuestionnaireUseCase;
import br.com.portal_nr1.domain.model.Questionnaire;
import br.com.portal_nr1.domain.model.Questionnaires;
import br.com.portal_nr1.domain.model.User;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.QuestionnaireRequestItem;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.QuestionnaireResponse;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.QuestionnaireResponseItem;
import br.com.portal_nr1.infrastructure.adapters.in.web.mapper.AuthenticatedUserMapper;
import br.com.portal_nr1.infrastructure.adapters.in.web.mapper.QuestionnaireMapper;
import br.com.portal_nr1.infrastructure.security.AuthenticatedUser;
import br.com.portal_nr1.infrastructure.security.AuthenticatedUserResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/questionnaires")
@RequiredArgsConstructor
public class QuestionnaireController {

    private final FetchQuestionnairesUseCase fetchQuestionnaireUseCase;
    private final FetchQuestionnaireByVersionUseCase fetchQuestionnaireByVersionUseCase;
    private final SaveQuestionnaireUseCase saveQuestionnaireUseCase;
    private final AuthenticatedUserResolver authenticatedUserResolver;

    @GetMapping("/")
    @SecurityRequirement(name = "oauth2")
    @Operation(summary = "Dados do usuário autenticado")
    public QuestionnaireResponse questionnaires() {
        Questionnaires questionnaires = fetchQuestionnaireUseCase.fetchQuestionnaires();
        return QuestionnaireMapper.toResponse(questionnaires);

    }

    @GetMapping("/{version}")
    @SecurityRequirement(name = "oauth2")
    @Operation(summary = "Dados do usuário autenticado")
    public QuestionnaireResponseItem questionnaire(@PathParam("version") String version) {
        Questionnaire questionnaire = fetchQuestionnaireByVersionUseCase.fetchQuestionnaire(version);
        return QuestionnaireMapper.toResponse(questionnaire);
    }

    @PostMapping("/versions")
    @SecurityRequirement(name = "oauth2")
    @Operation(summary = "Dados do usuário autenticado")
    public QuestionnaireResponseItem questionnaire(@RequestBody QuestionnaireRequestItem request,
            Principal principal,
            Authentication authentication) {
        
        AuthenticatedUser authenticatedUser = authenticatedUserResolver.resolve(principal, authentication);

        User user = AuthenticatedUserMapper.toDomain(authenticatedUser);
        Questionnaire input = QuestionnaireMapper.toDomain(request);
        
        Questionnaire questionnaire = saveQuestionnaireUseCase.save(input, user);
        return QuestionnaireMapper.toResponse(questionnaire);

    }

}
