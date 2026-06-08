package br.com.portal_nr1.application.ports.in;

import br.com.portal_nr1.domain.model.Group;
import br.com.portal_nr1.domain.model.GroupEntity;

public interface UpdateGroupUseCase {
    Group update(String id, GroupEntity group);
}
