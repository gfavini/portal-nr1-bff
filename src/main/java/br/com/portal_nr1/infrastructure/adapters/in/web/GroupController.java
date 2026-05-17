package br.com.portal_nr1.infrastructure.adapters.in.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.portal_nr1.application.ports.in.FetchGroupsUseCase;
import br.com.portal_nr1.application.ports.in.ProvisionGroupUseCase;
import br.com.portal_nr1.application.ports.in.UpdateGroupUseCase;
import br.com.portal_nr1.domain.model.Group;
import br.com.portal_nr1.domain.model.Groups;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.GroupCreatedResponse;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.GroupRequestItem;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.GroupResponseItem;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.GroupsResponse;
import br.com.portal_nr1.infrastructure.adapters.in.web.mapper.GroupsMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final FetchGroupsUseCase fetchGroupsUseCase;
    private final ProvisionGroupUseCase saveGroupUseCase;
    private final UpdateGroupUseCase updateGroupUseCase;

    @GetMapping("/")
    public GroupsResponse listGroups() {
        Groups groups = fetchGroupsUseCase.fetch();
        return GroupsMapper.toResponse(groups);
    }

    @PostMapping("/")
    public GroupCreatedResponse createGroup(@Valid @RequestBody GroupRequestItem request) {
        Group group = GroupsMapper.toDomain(request);
        String provisionedGroup = saveGroupUseCase.create(group);
        return GroupsMapper.toResponse(provisionedGroup);
    } 

    @PutMapping("/{id}")
    public GroupResponseItem updateGroup(@PathVariable String id, @RequestBody GroupRequestItem request) {
        Group group = GroupsMapper.toDomain(request);
        Group responseItem = updateGroupUseCase.update(id, group);
        return GroupsMapper.toResponse(responseItem);
    }

    
    
}
