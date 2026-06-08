package br.com.portal_nr1.application.ports.out;

import java.util.List;

import br.com.portal_nr1.domain.model.GroupEntity;

public interface GroupRepositoyPort {
    List<GroupEntity>  fetchAll();
    GroupEntity save(GroupEntity group);
    GroupEntity findById(String id);
    void deleteById(String groupId);
    void addDepartment(String id, String department);
}
