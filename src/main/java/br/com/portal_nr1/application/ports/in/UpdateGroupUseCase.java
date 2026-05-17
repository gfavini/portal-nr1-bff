package br.com.portal_nr1.application.ports.in;

import br.com.portal_nr1.domain.model.Group;

public interface UpdateGroupUseCase {
    Group update(String id, Group group);
}
