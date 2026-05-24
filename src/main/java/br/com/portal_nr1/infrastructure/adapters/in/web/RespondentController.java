package br.com.portal_nr1.infrastructure.adapters.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.portal_nr1.application.ports.in.CreateRespondentUseCase;
import br.com.portal_nr1.domain.model.Respondent;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.RespondentCreatedResponse;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.RespondetRequestItem;
import br.com.portal_nr1.infrastructure.adapters.in.web.mapper.RespondentMapper;

import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/respondents")
@RequiredArgsConstructor
public class RespondentController {

    private final CreateRespondentUseCase createRespondentUseCase;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public RespondentCreatedResponse createGroup(@Valid @RequestBody RespondetRequestItem request) {
        Respondent respondent = RespondentMapper.toDomain(request);
        Respondent provisionedRespondent = createRespondentUseCase.create(respondent);
        return RespondentMapper.toResponse(provisionedRespondent, RespondentCreatedResponse.class);
    }

}
