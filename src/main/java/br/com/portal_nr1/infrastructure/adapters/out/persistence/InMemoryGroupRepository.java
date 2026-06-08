package br.com.portal_nr1.infrastructure.adapters.out.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import br.com.portal_nr1.application.ports.out.GroupRepositoyPort;
import br.com.portal_nr1.domain.model.GroupEntity;


public class InMemoryGroupRepository implements GroupRepositoyPort {
    
    private final Map<String, GroupEntity> groups = new ConcurrentHashMap<>();

    @Override
    public List<GroupEntity> fetchAll() {
        return new ArrayList<>(groups.values());
    }

    @Override
    public GroupEntity save(GroupEntity group) {
        if(group.getId() == null)group.setId(UUID.randomUUID().toString());
        groups.put(group.getId(), group);
        return group;
    }

    @Override
    public GroupEntity findById(String id) {
        return groups.get(id);
    }

    @Override
    public void deleteById(String groupId) {
        groups.remove(groupId);
    }

    @Override
    public void addDepartment(String id, String department) {
        if(department.isEmpty() || department == null){
            return;
        }
        groups.get(id).getDepartments().add(department);
        //TODO: validar se o departamento existe antes de salvar??
    }
    
}
