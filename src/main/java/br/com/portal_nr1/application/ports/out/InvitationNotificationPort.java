package br.com.portal_nr1.application.ports.out;

import java.util.ArrayList;

public interface InvitationNotificationPort {

    void sendGroupInvitations(ArrayList<String> emails);

    void sendProvisionedRespondentTemporaryPassword(String email, String temporaryPassword);

}
