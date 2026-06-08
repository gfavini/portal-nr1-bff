package br.com.portal_nr1.infrastructure.adapters.out.persistence;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import br.com.portal_nr1.application.ports.out.GroupRepositoyPort;
import br.com.portal_nr1.domain.model.GroupEntity;
import lombok.RequiredArgsConstructor;

@Repository
@Primary
@RequiredArgsConstructor
public class GroupRepositoryJpaAdapter implements GroupRepositoyPort {

    private final GroupRepositoryJpa jpaRepository;

    @Override
    public List<GroupEntity> fetchAll() {
        return jpaRepository.findAll();
    }

    @Override
    public GroupEntity save(GroupEntity group) {
        return jpaRepository.save(group);
    }

    @Override
    public GroupEntity findById(String id) {
        return jpaRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(String groupId) {
        jpaRepository.deleteById(groupId);
    }

    @Override
    public void addDepartment(String id, String department) {
        if (department == null || department.isEmpty()) {
            return;
        }

        GroupEntity group = jpaRepository.findById(id).orElse(null);
        if (group != null) {
            group.getDepartments().add(department);
            jpaRepository.save(group);
        }
    }
}
