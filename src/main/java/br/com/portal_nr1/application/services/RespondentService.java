package br.com.portal_nr1.application.services;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.portal_nr1.application.exception.GroupNotFoundException;
import br.com.portal_nr1.application.exception.RespondentEmailConflictException;
import br.com.portal_nr1.application.exception.RespondentNotFoundException;
import br.com.portal_nr1.application.ports.in.CreateRespondentUseCase;
import br.com.portal_nr1.application.ports.in.GetFilteredRespondentsUserCase;
import br.com.portal_nr1.application.ports.in.UpdateRespondentUseCase;
import br.com.portal_nr1.application.ports.out.GroupRepositoyPort;
import br.com.portal_nr1.application.ports.out.InvitationNotificationPort;
import br.com.portal_nr1.application.ports.out.RespondentRepositoryPort;
import br.com.portal_nr1.application.ports.out.UserIdentityProvisioningPort;
import br.com.portal_nr1.domain.model.GroupEntity;
import br.com.portal_nr1.domain.model.ProvisionedRespondent;
import br.com.portal_nr1.domain.model.Respondent;
import br.com.portal_nr1.infrastructure.adapters.exception.UserEmailConflictException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RespondentService
        implements CreateRespondentUseCase, GetFilteredRespondentsUserCase, UpdateRespondentUseCase {

    private final RespondentRepositoryPort respondentRepository;
    private final GroupRepositoyPort groupRepositoy;
    private final UserIdentityProvisioningPort userIdentityProvisioning;
    private final InvitationNotificationPort notification;

    @Override
    public Respondent create(Respondent respondent) {
        if (respondent.getCreatedAt() == null)
            respondent.setCreatedAt(Instant.now());

        GroupEntity group = groupRepositoy.findById(respondent.getGroup().getId());
        if(group == null) {
            throw new GroupNotFoundException("Group not found with ID: " + respondent.getGroup().getId());
        }
        respondent.setGroup(group);

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
            respondent.setId(provisionedRespondent.id());
            Respondent result = respondentRepository.save(respondent);
            notification.sendProvisionedRespondentTemporaryPassword(respondent.getEmail(),
                    provisionedRespondent.temporaryPassword());

            // Atualiza a lista de departamentos
            groupRepositoy.addDepartment(group.getId(), result.getDepartment());

            return result;
        } catch (UserEmailConflictException ex) {
            throw new RespondentEmailConflictException(
                    "Respondent with email " + respondent.getEmail() + " already exists in the identity provider.");
        }
    }

    @Override
    public List<Respondent> fetch(String groupId, String firstName, String lastName, String department) {
        List<Respondent> response = respondentRepository.findByFilters(groupId, firstName, lastName, department);
        return response;
    }

    @Override
    public Respondent update(String id, Respondent updated) {
        Respondent current = respondentRepository.findById(id);
        if (current == null) {
            throw new RespondentNotFoundException("Respondent not found with id: " + id);
        }

        updated.setId(current.getId());

        Field[] campos = updated.getClass().getDeclaredFields();

        if (!updated.getGroup().getId().equals(current.getGroup().getId())) {
            updated.setGroup(current.getGroup());
        }

        for (Field campo : campos) {
            try {
                campo.setAccessible(true);

                Object valorAtualziado = campo.get(updated);
                Object valorAtual = campo.get(current);

                if (valorAtualziado == null) {
                    campo.set(updated, valorAtual);
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        groupRepositoy.addDepartment(updated.getGroup().getId(), updated.getDepartment());
        
        return respondentRepository.save(updated);

    }

}
