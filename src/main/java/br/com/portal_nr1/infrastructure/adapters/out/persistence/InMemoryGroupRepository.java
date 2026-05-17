package br.com.portal_nr1.infrastructure.adapters.out.persistence;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import br.com.portal_nr1.application.ports.out.GroupRepositoyPort;
import br.com.portal_nr1.domain.model.Group;
import br.com.portal_nr1.domain.model.Groups;


@Repository
public class InMemoryGroupRepository implements GroupRepositoyPort {
    
    private final Map<String, Group> groups = new ConcurrentHashMap<>();

    @Override
    public Groups fetchAll() {
        return new Groups(new ArrayList<>(groups.values()));
    }

    @Override
    public void save(Group group) {
        groups.put(group.getId(), group);
    }

    @Override
    public Group findById(String id) {
        return groups.get(id);
    }
    
}
