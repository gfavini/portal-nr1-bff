package br.com.portal_nr1.infrastructure.adapters.in.web.mapper;

import java.util.ArrayList;
import java.util.stream.Collectors;

import br.com.portal_nr1.domain.model.Group;
import br.com.portal_nr1.domain.model.Groups;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.GroupCreatedResponse;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.GroupRequestItem;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.GroupResponseItem;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.GroupStatusResponse;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.GroupsResponse;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.InviteScopeResponse;

public final class GroupsMapper {

    public static GroupsResponse toResponse(Groups groups) {
        if (groups == null) {
            return new GroupsResponse(new ArrayList<>());
        }

        ArrayList<GroupResponseItem> items = toItems(groups);
        return new GroupsResponse(items);
    }

    public static GroupCreatedResponse toResponse(String groupId) {
        if (groupId == null) {
            return null;
        }
        return new GroupCreatedResponse(groupId);
    }

    private static ArrayList<GroupResponseItem> toItems(Groups groups) {
        if (groups == null || groups.groups() == null || groups.groups().isEmpty()) {
            return new ArrayList<>();
        }

        return groups.groups().stream()
                .map(GroupsMapper::toItem)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private static GroupResponseItem toItem(Group group) {
        if (group == null) {
            return null;
        }

        return new GroupResponseItem(
                group.getId(),
                group.getName(),
                group.getRespondentCount(),
                group.getDepartments(),
                group.getAssignedQuestionnaireId(),
                group.getAssignedQuestionnaireVersion(),
                GroupStatusResponse.fromDomain(group.getStatus()),
                group.getExpiresAt(),
                group.getLastInviteSentAt(),
                InviteScopeResponse.fromDomain(group.getLastInviteScope()));
    }

    public static Group toDomain(GroupRequestItem request) {
        if (request == null) {
            return null;
        }

        return new Group(
                null,
                request.name(),
                null,
                null,
                request.assignedQuestionnaireId(),
                request.assignedQuestionnaireVersion(),
                null,
                request.expiresAt(),
                null,
                null);
    }

    public static GroupResponseItem toResponse(Group responseItem) {
        return toItem(responseItem);
    }
}
