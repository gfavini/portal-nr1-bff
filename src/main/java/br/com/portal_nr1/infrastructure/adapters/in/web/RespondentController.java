package br.com.portal_nr1.infrastructure.adapters.in.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.portal_nr1.application.ports.in.CreateRespondentUseCase;
import br.com.portal_nr1.application.ports.in.GetFilteredRespondentsUserCase;
import br.com.portal_nr1.application.ports.in.UpdateRespondentUseCase;
import br.com.portal_nr1.domain.model.Respondent;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.RespondentCreatedResponse;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.RespondentListResponse;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.RespondentRequestItem;
import br.com.portal_nr1.infrastructure.adapters.in.web.mapper.RespondentMapper;

import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/respondents")
@RequiredArgsConstructor
public class RespondentController {

    private final CreateRespondentUseCase createRespondentUseCase;
    private final UpdateRespondentUseCase updateRespondentUseCase;
    private final GetFilteredRespondentsUserCase getFilteredRespondentsUserCase;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public RespondentCreatedResponse createRespondent(@Valid @RequestBody RespondentRequestItem request) {
        Respondent respondent = RespondentMapper.toDomain(request);
        Respondent provisionedRespondent = createRespondentUseCase.create(respondent);
        return RespondentMapper.toResponse(provisionedRespondent, RespondentCreatedResponse.class);
    }

    @GetMapping("")
    public RespondentListResponse getFilteredRespondents(
        @RequestParam(name = "groupId", required = true) String groupId,
        @RequestParam(name = "firstName", required = false) String firstName,
        @RequestParam(name = "lastName", required = false) String lastName,
        @RequestParam(name = "department", required = false) String department) {

        List<Respondent> respondents = getFilteredRespondentsUserCase.fetch(groupId, firstName, lastName, department);
        return RespondentMapper.toResponse(respondents);
    }


    @PutMapping("/{id}")
    public RespondentCreatedResponse updateRespondent(@Valid @RequestBody RespondentRequestItem request, @PathVariable String id) {
        Respondent respondent = RespondentMapper.toDomain(request);
        Respondent provisionedRespondent = updateRespondentUseCase.update(id, respondent);
        return RespondentMapper.toResponse(provisionedRespondent, RespondentCreatedResponse.class);
    }

}
