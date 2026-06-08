package br.com.portal_nr1.infrastructure.adapters.in.web.mapper;

import java.util.ArrayList;
import java.util.stream.Collectors;

import br.com.portal_nr1.domain.model.Group;
import br.com.portal_nr1.domain.model.GroupEntity;
import br.com.portal_nr1.domain.model.Groups;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.GroupClosedResponse;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.GroupCreatedResponse;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.GroupDeletedResponse;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.GroupReopenedReponse;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.GroupRequestItem;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.GroupResponseItem;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.GroupStatusResponse;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.GroupUpdatedResponse;
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

    public static GroupCreatedResponse toCreatedResponse(String groupId) {
        if (groupId == null) {
            return null;
        }
        return new GroupCreatedResponse(groupId);
    }

    public static GroupDeletedResponse toDeletedResponse(String groupId) {
        if (groupId == null) {
            return null;
        }
        return new GroupDeletedResponse(groupId);
    }

    public static GroupUpdatedResponse toUpdatedResponse(Group responseItem) {
        return new GroupUpdatedResponse(toItem(responseItem));
    }

    public static GroupClosedResponse toClosedResponse(Group responseItem) {
        return new GroupClosedResponse(toItem(responseItem));
    }

    // FIXME : O anti-padrão me fodendo de novo.... mais facil criar uma classe de
    // retorno da camada de aplicação
    public static <T> T toResponse(String groupId, Class<T> targetClass) {
        if (targetClass.equals(GroupCreatedResponse.class)) {
            return targetClass.cast(toCreatedResponse(groupId));
        } else if (targetClass.equals(GroupDeletedResponse.class)) {
            return targetClass.cast(toDeletedResponse(groupId));
        }
        throw new IllegalArgumentException("Tipo de resposta não suportado: " + targetClass.getName());
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

    public static GroupEntity toDomain(GroupRequestItem request) {
        if (request == null) {
            return null;
        }

        return GroupEntity.builder()
            .name(request.name())
            .assignedQuestionnaireId(request.assignedQuestionnaireId())
            .assignedQuestionnaireVersion(request.assignedQuestionnaireVersion())
            .expiresAt(request.expiresAt())
            .build();
    }

    public static GroupReopenedReponse toReopenedGroupResponse(Group responseItem) {
        return new GroupReopenedReponse(
            toItem(responseItem)
        );

    }

    public static Group fromEntity(GroupEntity entity, Integer respondentCount) {
        return new Group(
            entity.getId(),
            entity.getName(),
            respondentCount,
            entity.getDepartments(),
            entity.getAssignedQuestionnaireId(),
            entity.getAssignedQuestionnaireVersion(),
            entity.getStatus(),
            entity.getExpiresAt(),
            entity.getLastInviteSentAt(),
            entity.getLastInviteScope(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

}
