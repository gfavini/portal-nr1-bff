package br.com.portal_nr1.application.ports.in;

import br.com.portal_nr1.domain.model.Group;

public interface CloseGroupUseCase {
    public Group close (String groupId);
}
