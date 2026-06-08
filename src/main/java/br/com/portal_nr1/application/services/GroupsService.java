package br.com.portal_nr1.application.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.portal_nr1.application.exception.GroupClosedException;
import br.com.portal_nr1.application.exception.GroupExepiredException;
import br.com.portal_nr1.application.exception.GroupNotFoundException;
import br.com.portal_nr1.application.exception.QuestionnaireNotFoundException;
import br.com.portal_nr1.application.ports.in.CloseGroupUseCase;
import br.com.portal_nr1.application.ports.in.DeleteGroupUseCase;
import br.com.portal_nr1.application.ports.in.FetchGroupsUseCase;
import br.com.portal_nr1.application.ports.in.ProvisionGroupUseCase;
import br.com.portal_nr1.application.ports.in.ReopenGroupUseCase;
import br.com.portal_nr1.application.ports.in.UpdateGroupUseCase;
import br.com.portal_nr1.application.ports.out.GroupRepositoyPort;
import br.com.portal_nr1.application.ports.out.QuestionnaireRespositoryPort;
import br.com.portal_nr1.application.ports.out.RespondentRepositoryPort;
import br.com.portal_nr1.domain.model.Group;
import br.com.portal_nr1.domain.model.GroupEntity;
import br.com.portal_nr1.domain.model.GroupStatus;
import br.com.portal_nr1.domain.model.Groups;
import br.com.portal_nr1.domain.model.Questionnaire;
import br.com.portal_nr1.infrastructure.adapters.in.web.mapper.GroupsMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupsService implements FetchGroupsUseCase,
        ProvisionGroupUseCase,
        UpdateGroupUseCase,
        DeleteGroupUseCase,
        CloseGroupUseCase,
        ReopenGroupUseCase {

    private final GroupRepositoyPort groupsRepositoy;
    private final QuestionnaireRespositoryPort questionnaireRepository;
    private final RespondentRepositoryPort respondentRepository;

    @Override
    public Groups fetch() {
        List<GroupEntity> entities = groupsRepositoy.fetchAll();
        List<Group> result = new ArrayList<>();

        Map<String, Integer> respondendetCount = respondentRepository.countByGroupIds();

        // Verificar se algum grupo expirou e atualizar seu status
        for (GroupEntity entity : entities) {
            if (entity.getExpiresAt() != null && entity.getExpiresAt().isBefore(Instant.now())) {
                entity.setStatus(GroupStatus.EXPIRED);
                groupsRepositoy.save(entity);
            }

            Integer count = Optional.ofNullable(respondendetCount.get(entity.getId())).orElse(0);
            Group group = GroupsMapper.fromEntity(entity, count);
            result.add(group);
        }
        return new Groups(result);
    }

    @Override
    public String create(GroupEntity group) {

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

        GroupEntity groupToSave = GroupEntity.builder()
                .name(group.getName())
                .assignedQuestionnaireId(assignedQuestionnaireId)
                .assignedQuestionnaireVersion(assignedQuestionnaireVersion)
                .status(status)
                .expiresAt(expiresAt)
                .build();

        GroupEntity saved = groupsRepositoy.save(groupToSave);
        return saved.getId();
    }

    @Override
    public Group update(String id, GroupEntity group) {
        GroupEntity existingGroup = groupsRepositoy.findById(id);

        if (existingGroup == null) {
            throw new GroupNotFoundException("Group not found with id: " + id);
        }
        if (existingGroup.getStatus() == GroupStatus.CLOSED) {
            throw new GroupClosedException("Cannot update a closed group.");
        }

        if (group.getName() != null && !group.getName().isBlank()) {
            existingGroup.setName(group.getName());
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

        GroupEntity saved = groupsRepositoy.save(existingGroup);
        int respondendetCount = respondentRepository.countByGroupId(saved.getId());

        return GroupsMapper.fromEntity(saved, respondendetCount);
    }

    @Override
    public String delete(String groupId) {

        groupsRepositoy.deleteById(groupId);
        return groupId;

    }

    @Override
    public Group close(String groupId) {
        GroupEntity toUpdate = groupsRepositoy.findById(groupId);

        if (toUpdate == null) {
            throw new GroupNotFoundException("Group not found with id: " + groupId);
        }

        if (toUpdate.getStatus() == GroupStatus.EXPIRED) {
            if(toUpdate.getExpiresAt().isBefore(Instant.now())) {
                toUpdate.setStatus(GroupStatus.EXPIRED);
                groupsRepositoy.save(toUpdate);
            }
            throw new GroupExepiredException("Group already expired with ID: " + groupId);
        }

        toUpdate.setStatus(GroupStatus.CLOSED);
        GroupEntity updated = groupsRepositoy.save(toUpdate);

        int respondendetCount = respondentRepository.countByGroupId(updated.getId());
        return GroupsMapper.fromEntity(updated, respondendetCount);
    }

    @Override
    public Group reopen(String groupId) {
        GroupEntity group = groupsRepositoy.findById(groupId);

        if (group == null) {
            throw new GroupNotFoundException("Group not found with id " + groupId);
        }

        if (group.getStatus() == GroupStatus.EXPIRED) {
            if(group.getExpiresAt().isBefore(Instant.now())) {
                group.setStatus(GroupStatus.EXPIRED);
                groupsRepositoy.save(group);
            }
            throw new GroupExepiredException("Group already expired with ID: " + groupId);
        }

        group.setStatus(GroupStatus.OPEN);
        GroupEntity groupUpdated = groupsRepositoy.save(group);

        int respondendetCount = respondentRepository.countByGroupId(groupUpdated.getId());
        return GroupsMapper.fromEntity(groupUpdated, respondendetCount);
    }

}
