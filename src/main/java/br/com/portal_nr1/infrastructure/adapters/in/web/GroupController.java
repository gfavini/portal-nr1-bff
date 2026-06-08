package br.com.portal_nr1.infrastructure.adapters.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.portal_nr1.application.ports.in.CloseGroupUseCase;
import br.com.portal_nr1.application.ports.in.DeleteGroupUseCase;
import br.com.portal_nr1.application.ports.in.FetchGroupsUseCase;
import br.com.portal_nr1.application.ports.in.ProvisionGroupUseCase;
import br.com.portal_nr1.application.ports.in.ReopenGroupUseCase;
import br.com.portal_nr1.application.ports.in.UpdateGroupUseCase;
import br.com.portal_nr1.domain.model.Group;
import br.com.portal_nr1.domain.model.GroupEntity;
import br.com.portal_nr1.domain.model.Groups;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.GroupClosedResponse;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.GroupCreatedResponse;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.GroupDeletedResponse;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.GroupReopenedReponse;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.GroupRequestItem;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.GroupUpdatedResponse;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.GroupsResponse;
import br.com.portal_nr1.infrastructure.adapters.in.web.mapper.GroupsMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final FetchGroupsUseCase fetchGroupsUseCase;
    private final ProvisionGroupUseCase saveGroupUseCase;
    private final UpdateGroupUseCase updateGroupUseCase;
    private final DeleteGroupUseCase deleteGroupUseCase;
    private final CloseGroupUseCase closeGroupUseCase;
    private final ReopenGroupUseCase reopenGroupUseCase;

    @GetMapping("/")
    public GroupsResponse listGroups() {
        Groups groups = fetchGroupsUseCase.fetch();
        return GroupsMapper.toResponse(groups);
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public GroupCreatedResponse createGroup(@Valid @RequestBody GroupRequestItem request) {
        GroupEntity group = GroupsMapper.toDomain(request);
        //FIXME: Talvez seja anti-pdrão retornar um "primitivo"
        String provisionedGroup = saveGroupUseCase.create(group);
        return GroupsMapper.toResponse(provisionedGroup, GroupCreatedResponse.class);
    } 

    @PutMapping("/{id}")
    public GroupUpdatedResponse updateGroup(@PathVariable String id, @RequestBody GroupRequestItem request) {
        GroupEntity group = GroupsMapper.toDomain(request);
        Group responseItem = updateGroupUseCase.update(id, group);
        return GroupsMapper.toUpdatedResponse(responseItem);
    }

    @DeleteMapping("/{id}")
    public GroupDeletedResponse deleteGroup(@PathVariable String id) {
        //FIXME: Talvez seja anti-pdrão retornar um "primitivo"
        String responseItem = deleteGroupUseCase.delete(id);
        return GroupsMapper.toResponse(responseItem, GroupDeletedResponse.class);
    }

    @PostMapping("/{id}/close")
    public GroupClosedResponse closeGroup(@PathVariable String id) {
        Group responseItem = closeGroupUseCase.close(id);
        return GroupsMapper.toClosedResponse(responseItem);
    }

    @PostMapping("/{id}/reopen")
    public GroupReopenedReponse postMethodName(@PathVariable String id) {
        Group responseItem = reopenGroupUseCase.reopen(id);
        return GroupsMapper.toReopenedGroupResponse(responseItem);
    }
    

}
