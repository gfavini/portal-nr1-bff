package br.com.portal_nr1.application.ports.out;

import br.com.portal_nr1.domain.model.Group;
import br.com.portal_nr1.domain.model.Groups;

public interface GroupRepositoyPort {
    Groups fetchAll();
    Group save(Group group);
    Group findById(String id);
    void deleteById(String groupId);
}
