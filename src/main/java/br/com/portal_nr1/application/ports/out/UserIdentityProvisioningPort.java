package br.com.portal_nr1.application.ports.out;

import br.com.portal_nr1.domain.model.ProvisionedRespondent;
import br.com.portal_nr1.domain.model.Respondent;

public interface UserIdentityProvisioningPort {    
    ProvisionedRespondent provisionRespondent(Respondent respondent);
    String provisionGroup(String groupName);
    void assignRespondentToGroup(String groupId, ProvisionedRespondent respondent);
    ProvisionedRespondent provisionRespondentAndAssignGroup(Respondent respondent, String groupId);
    String updateGroupName(String id, String name);
}
