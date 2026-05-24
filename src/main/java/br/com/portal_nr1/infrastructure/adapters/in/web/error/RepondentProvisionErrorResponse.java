package br.com.portal_nr1.infrastructure.adapters.in.web.error;

import br.com.portal_nr1.infrastructure.adapters.in.web.dto.RespondetResponseItem;

public record RepondentProvisionErrorResponse(
        String code,
        String message,
        Conflict conflict) {
            
    public record Conflict(
            RespondetResponseItem respondent,
            String currentGroupId,
            String currentGroupName) {
    }
}
