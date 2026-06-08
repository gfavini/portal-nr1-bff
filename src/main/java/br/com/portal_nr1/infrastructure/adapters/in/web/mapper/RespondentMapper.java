package br.com.portal_nr1.infrastructure.adapters.in.web.mapper;

import java.util.List;

import br.com.portal_nr1.domain.model.GroupEntity;
import br.com.portal_nr1.domain.model.Respondent;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.RespondentCreatedResponse;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.RespondentGender;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.RespondentListResponse;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.RespondentRequestItem;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.RespondentResponseItem;

public final class RespondentMapper {

    public static Respondent toDomain(RespondentRequestItem request) {
        return Respondent.builder()
        .firstName(request.firstName())
        .lastName(request.lastName())
        .email(request.email())
        .age(request.age())
        .department(request.department())
        .gender(RespondentGender.toDomain(request.gender()))
        .group(GroupEntity.builder().id(request.groupId()).build())
        .build();
    }

    private static RespondentCreatedResponse toCreatedResponse(Respondent provisionedRespondent) {
        return new RespondentCreatedResponse(provisionedRespondent.getId(),
                provisionedRespondent.getFirstName(), provisionedRespondent.getLastName(),
                provisionedRespondent.getEmail(), provisionedRespondent.getAge(),
                provisionedRespondent.getDepartment(), RespondentGender.fromDomain(provisionedRespondent.getGender()),
                provisionedRespondent.getGroup().getId(), provisionedRespondent.getGroup().getName());
    }

    private static RespondentResponseItem toItem(Respondent provisionedRespondent) {
        return new RespondentResponseItem(provisionedRespondent.getId(),
                provisionedRespondent.getFirstName(), provisionedRespondent.getLastName(),
                provisionedRespondent.getEmail(), provisionedRespondent.getAge(),
                provisionedRespondent.getDepartment(), RespondentGender.fromDomain(provisionedRespondent.getGender()),
                provisionedRespondent.getGroup().getId(), provisionedRespondent.getGroup().getName());
    }

    public static <T> T toResponse(Respondent provisionedRespondent, Class<T> targetClass) {
        if (targetClass.equals(RespondentCreatedResponse.class)) {
            return targetClass.cast(toCreatedResponse(provisionedRespondent));
        } if (targetClass.equals(RespondentResponseItem.class)) {
            return targetClass.cast(toItem(provisionedRespondent));
        }
        throw new IllegalArgumentException("Tipo de resposta não suportado: " + targetClass.getName());
    }

    public static RespondentListResponse toResponse(List<Respondent> respondents) {

        List<RespondentResponseItem> items = respondents.stream().map(RespondentMapper::toItem).toList();
        return new RespondentListResponse(items, items.size());
    }

}
