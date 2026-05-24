package br.com.portal_nr1.application.services;

import java.time.Instant;
import java.util.HashSet;

import org.springframework.stereotype.Service;

import br.com.portal_nr1.application.exception.RespondentEmailConflictException;
import br.com.portal_nr1.application.ports.in.CreateRespondentUseCase;
import br.com.portal_nr1.application.ports.out.InvitationNotificationPort;
import br.com.portal_nr1.application.ports.out.GroupRepositoyPort;
import br.com.portal_nr1.application.ports.out.RespondentRepositoryPort;
import br.com.portal_nr1.application.ports.out.UserIdentityProvisioningPort;
import br.com.portal_nr1.domain.model.Group;
import br.com.portal_nr1.domain.model.ProvisionedRespondent;
import br.com.portal_nr1.domain.model.Respondent;
import br.com.portal_nr1.infrastructure.adapters.exception.KeycloakEmailConflictException;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class RespondentService implements CreateRespondentUseCase {

    private final RespondentRepositoryPort respondentRepository;
    private final GroupRepositoyPort groupRepositoy;
    private final UserIdentityProvisioningPort userIdentityProvisioning;
    private final InvitationNotificationPort notification;

    @Override
    public Respondent create(Respondent respondent) {
        Respondent respToSave = Respondent.copy(respondent);
        if(respondent.getCreatedAt() == null) respToSave.setCreatedAt(Instant.now());

        Group group = groupRepositoy.findById(respondent.getGroupId());
        if(respondent.getGroupName() == null) {
            respToSave.setGroupName(group.getName());
        }

        Respondent tmp = respondentRepository.getGroupByEmail(respondent.getEmail());
        // checa se ja existe registro deste usuario em outro grupo
        if (tmp != null) {
            throw new RespondentEmailConflictException(
                "RESPONDENT_EMAIL_CONFLICT",
                "E-mail already assigned to another group.",
                tmp);
        }

        try {
            ProvisionedRespondent provisionedRespondent = userIdentityProvisioning.provisionRespondent(respondent);
            respToSave.setId(provisionedRespondent.id());
            Respondent result = respondentRepository.save(respToSave);   
            notification.sendProvisionedRespondentTemporaryPassword(respToSave.getEmail(), provisionedRespondent.temporaryPassword());
            
            // Atualiza a lista de departamentos
            groupRepositoy.addDepartment(group.getId(), result.getDepartment());
            
            return result;
        } catch(KeycloakEmailConflictException ex) {
            throw new RespondentEmailConflictException("Respondent with email " + respondent.getEmail() + " already exists in the identity provider.");
        }
    }
    
}
