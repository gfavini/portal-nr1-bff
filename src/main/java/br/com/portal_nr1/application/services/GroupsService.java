package br.com.portal_nr1.application.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import org.springframework.stereotype.Service;

import br.com.portal_nr1.application.exception.GroupAlreadyExistsException;
import br.com.portal_nr1.application.exception.GroupClosedException;
import br.com.portal_nr1.application.exception.GroupNotFoundException;
import br.com.portal_nr1.application.exception.QuestionnaireNotFoundException;
import br.com.portal_nr1.application.ports.in.FetchGroupsUseCase;
import br.com.portal_nr1.application.ports.in.ProvisionGroupUseCase;
import br.com.portal_nr1.application.ports.in.UpdateGroupUseCase;
import br.com.portal_nr1.application.ports.out.GroupRepositoyPort;
import br.com.portal_nr1.application.ports.out.QuestionnaireRespositoryPort;
import br.com.portal_nr1.application.ports.out.UserIdentityProvisioningPort;
import br.com.portal_nr1.domain.model.Group;
import br.com.portal_nr1.domain.model.GroupStatus;
import br.com.portal_nr1.domain.model.Groups;
import br.com.portal_nr1.domain.model.Questionnaire;
import br.com.portal_nr1.infrastructure.adapters.exception.KeycloakGroupNameConflictException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupsService implements FetchGroupsUseCase, ProvisionGroupUseCase, UpdateGroupUseCase {

    private final GroupRepositoyPort groupsRepositoy;
    private final QuestionnaireRespositoryPort questionnaireRepository;
    private final UserIdentityProvisioningPort userIdentityProvisioningPort;

    @Override
    public Groups fetch() {
        Groups groups = groupsRepositoy.fetchAll();
        // Verificar se algum grupo expirou e atualizar seu status
        for (Group group : groups.groups()) {
            if (group.getExpiresAt() != null && group.getExpiresAt().isBefore(Instant.now())) {
                group.setStatus(GroupStatus.EXPIRED);
                groupsRepositoy.save(group);
            }
        }
        return groups;
    }

    @Override
    public String create(Group group) {
        String provisionedGroupId = null;
        try {
            provisionedGroupId = userIdentityProvisioningPort.provisionGroup(group.getName());
        } catch (KeycloakGroupNameConflictException e) {
            throw new GroupAlreadyExistsException("Group already exists in the identity provider.");
        }

        String assignedQuestionnaireId = group.getAssignedQuestionnaireId();
        Integer assignedQuestionnaireVersion = group.getAssignedQuestionnaireVersion();

        if (group.getAssignedQuestionnaireId() == null || assignedQuestionnaireId.isBlank()) {
            Questionnaire latestQuestionnaire = questionnaireRepository.fetchLatest();
            if (latestQuestionnaire == null) {
                throw new QuestionnaireNotFoundException("No questionnaire available to assign to the group.");
            }
            assignedQuestionnaireId = latestQuestionnaire.getId();
            assignedQuestionnaireVersion = latestQuestionnaire.getVersion();
        }

        Instant expiresAt = group.getExpiresAt() != null
                ? group.getExpiresAt()
                : Instant.now().plus(30, ChronoUnit.DAYS);

        GroupStatus status = group.getStatus() != null
                ? group.getStatus()
                : GroupStatus.OPEN;

        Group groupToSave = new Group(
                provisionedGroupId,
                group.getName(),
                0,
                new ArrayList<String>(),
                assignedQuestionnaireId,
                assignedQuestionnaireVersion,
                status,
                expiresAt,
                null,
                null);

        groupsRepositoy.save(groupToSave);
        return groupToSave.getId();
    }

    @Override
    public Group update(String id, Group group) {
        Group existingGroup = groupsRepositoy.findById(id);

        if (existingGroup == null) {
            throw new GroupNotFoundException("Group not found with id: " + id);
        }
        if (existingGroup.getStatus() == GroupStatus.CLOSED) {
            throw new GroupClosedException("Cannot update a closed group.");
        }

        if (!group.getName().equalsIgnoreCase(existingGroup.getName())){
            try {
                userIdentityProvisioningPort.updateGroupName(existingGroup.getId(), group.getName());
                existingGroup.setName(group.getName());
            } catch (KeycloakGroupNameConflictException e) {
                throw new GroupAlreadyExistsException("Another group with the same name already exists in the identity provider.", e);
            }
        }

        if (group.getAssignedQuestionnaireId() != null && !group.getAssignedQuestionnaireId().isBlank()) {
            existingGroup.setAssignedQuestionnaireId(group.getAssignedQuestionnaireId());
        }

        if (group.getAssignedQuestionnaireVersion() != null) {
            existingGroup.setAssignedQuestionnaireVersion(group.getAssignedQuestionnaireVersion());
        }

        if (group.getExpiresAt() != null && group.getExpiresAt().isAfter(Instant.now())) {
            existingGroup.setExpiresAt(group.getExpiresAt());
        }

        groupsRepositoy.save(existingGroup);
        return existingGroup;
    }

}
