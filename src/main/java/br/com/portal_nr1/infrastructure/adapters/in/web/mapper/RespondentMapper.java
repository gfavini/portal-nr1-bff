package br.com.portal_nr1.infrastructure.adapters.in.web.mapper;

import br.com.portal_nr1.domain.model.Respondent;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.GroupCreatedResponse;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.RespondentCreatedResponse;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.RespondentGender;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.RespondetRequestItem;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.RespondetResponseItem;

public final class RespondentMapper {

    public static Respondent toDomain(RespondetRequestItem request) {
        return new Respondent(
                null,
                request.firstName(),
                request.lastName(),
                request.email(),
                request.age(),
                request.department(),
                RespondentGender.toDomain(request.gender()),
                request.groupId(),
                null,
                null,
                request.firstName().toLowerCase(),
                request.lastName().toLowerCase());
    }

    private static RespondentCreatedResponse toCreatedResponse(Respondent provisionedRespondent) {
        return new RespondentCreatedResponse(toItem(provisionedRespondent));
    }

    private static RespondetResponseItem toItem(Respondent provisionedRespondent) {
        return new RespondetResponseItem(provisionedRespondent.getId(),
                provisionedRespondent.getFirstName(), provisionedRespondent.getLastName(),
                provisionedRespondent.getEmail(), provisionedRespondent.getAge(),
                provisionedRespondent.getDepartment(), RespondentGender.fromDomain(provisionedRespondent.getGender()),
                provisionedRespondent.getGroupId(), provisionedRespondent.getGroupName());
    }

    public static <T> T toResponse(Respondent provisionedRespondent, Class<T> targetClass) {
        if (targetClass.equals(RespondentCreatedResponse.class)) {
            return targetClass.cast(toCreatedResponse(provisionedRespondent));
        } if (targetClass.equals(RespondetResponseItem.class)) {
            return targetClass.cast(toItem(provisionedRespondent));
        }
        throw new IllegalArgumentException("Tipo de resposta não suportado: " + targetClass.getName());
    }

}
