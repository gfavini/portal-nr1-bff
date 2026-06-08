package br.com.portal_nr1.application.ports.in;

import br.com.portal_nr1.domain.model.GroupEntity;

public interface ProvisionGroupUseCase {
    String create(GroupEntity group);
}
