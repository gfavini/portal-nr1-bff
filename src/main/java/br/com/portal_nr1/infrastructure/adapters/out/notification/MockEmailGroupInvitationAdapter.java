package br.com.portal_nr1.infrastructure.adapters.out.notification;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import br.com.portal_nr1.application.ports.out.GroupInvitationNotificationPort;
import br.com.portal_nr1.domain.model.Group;
import lombok.extern.log4j.Log4j2;


@Component
@Log4j2
public class MockEmailGroupInvitationAdapter implements GroupInvitationNotificationPort {

    @Override
    public void sendGroupInvitations(ArrayList<String> emails) {
        log.info("MockEmailGroupInvitationAdapter: Enviando convites para o grupo '{}'", emails);
        for (var email : emails) {
            log.info("MockEmailGroupInvitationAdapter: Convite enviado para '{}'", email);
        }
    }

    @Override
    public void sendProvisionedRespondentTemporaryPassword(String email, String temporaryPassword) {
        log.info("MockEmailGroupInvitationAdapter: Enviando senha temporária para '{}' > Senha: {}", email, temporaryPassword);
    }
}
