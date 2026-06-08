package br.com.portal_nr1.infrastructure.adapters.in.web.dto;

public record RespondentCreatedResponse(
        String id,
        String firstName,
        String lastName,
        String email,
        Integer age,
        String department,
        RespondentGender gender,
        String groupId,
        String groupName
) {

}
