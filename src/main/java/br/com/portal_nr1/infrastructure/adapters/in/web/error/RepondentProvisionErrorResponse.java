package br.com.portal_nr1.infrastructure.adapters.in.web.error;

import br.com.portal_nr1.infrastructure.adapters.in.web.dto.RespondentResponseItem;

public record RepondentProvisionErrorResponse(
        String code,
        String message,
        Conflict conflict) {
            
    public record Conflict(
            RespondentResponseItem respondent,
            String currentGroupId,
            String currentGroupName) {
    }
}
