package br.com.portal_nr1.infrastructure.adapters.out.identity;

import java.util.ArrayList;
import java.util.Collections;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.portal_nr1.application.ports.out.UserIdentityProvisioningPort;
import br.com.portal_nr1.domain.model.ProvisionedRespondent;
import br.com.portal_nr1.domain.model.Respondent;
import br.com.portal_nr1.infrastructure.adapters.exception.KeycloakEmailConflictException;
import br.com.portal_nr1.infrastructure.adapters.exception.KeycloakGroupNameConflictException;
import br.com.portal_nr1.infrastructure.adapters.exception.KeycloakGroupNotFoundException;
import br.com.portal_nr1.infrastructure.adapters.exception.KeycloakProvisioningException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
@Service
public class KeycloakUserIdentityProvisioningAdapter implements UserIdentityProvisioningPort {

    private final Keycloak keycloak;

    @Value("${keycloak.respondent.role-name}")
    private String roleName;
    @Value("${keycloak.realm}")
    private String targetRealm;

    @Override
    public ProvisionedRespondent provisionRespondent(Respondent respondent) throws KeycloakProvisioningException {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(respondent.getEmail());
        user.setEmail(respondent.getEmail());
        user.setFirstName(respondent.getFirstName());
        user.setLastName(respondent.getLastName());
        user.setEnabled(true);

        Response response = keycloak.realm(targetRealm).users().create(user);

        if (response.getStatus() == 201) {
            // Get the unique ID created by Keycloak
            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(true);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(generateRandomPassword()); 
            
            UserResource userResource = keycloak.realm(targetRealm).users().get(userId);
            userResource.resetPassword(passwordCred);

            RoleRepresentation role = keycloak.realm(targetRealm).roles().get(roleName).toRepresentation();
            userResource.roles().realmLevel().add(Collections.singletonList(role));

            log.info("User created with ID: {}", userId);
            return new ProvisionedRespondent(userId, respondent.getEmail(), passwordCred.getValue());
        } else if (response.getStatus() == 409) {
            log.warn("User with email {} already exists", respondent.getEmail());
            throw new KeycloakEmailConflictException("A user with the same email already exists in Keycloak");
        } else {
            log.error("Failed to create user. Status: {}, Response: {}", response.getStatus(),
                    response.readEntity(String.class));
            throw new KeycloakProvisioningException("Failed to create user in Keycloak");
        }
    }

    @Override
    public String provisionGroup(String groupName) throws KeycloakProvisioningException {
        GroupRepresentation groupRepresentation = new GroupRepresentation();
        groupRepresentation.setName(groupName);

        Response response = keycloak.realm(targetRealm).groups().add(groupRepresentation);

        if (response.getStatus() == 201) {
            // Extract Group ID from the Location header
            String groupId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            log.info("Group created with ID: {}", groupId);
            return groupId;

        } else if (response.getStatus() == 409) {
            log.error("Group with name {} already exists", groupName);
            throw new KeycloakGroupNameConflictException("Another group with the same name already exists in Keycloak");
        } else {
            log.error("Failed to create group. Status: {}, Response: {}", response.getStatus(),
                    response.readEntity(String.class));
            throw new KeycloakProvisioningException("Failed to create group in Keycloak");
        }

    }

    @Override
    public void assignRespondentToGroup(String groupId, ProvisionedRespondent respondent)
            throws KeycloakProvisioningException {
        try {
            keycloak.realm(targetRealm).users().get(respondent.id()).joinGroup(groupId);
            log.info("User with ID: {} assigned to group with ID: {}", respondent.id(), groupId);
        } catch (WebApplicationException e) {
            log.error("Failed to assign user with ID: {} to group with ID: {}. Status: {}, Response: {}",
                    respondent.id(), groupId, e.getResponse().getStatus(), e.getResponse().readEntity(String.class));
            throw new KeycloakProvisioningException("Failed to assign user to group in Keycloak", e);
        }
    }

    @Override
    public ProvisionedRespondent provisionRespondentAndAssignGroup(Respondent respondent, String groupId)
            throws KeycloakProvisioningException {
        ProvisionedRespondent provisioned = provisionRespondent(respondent);
        if (provisioned != null) {
            assignRespondentToGroup(groupId, provisioned);
        }
        return provisioned;
    }

    @Override
    public String updateGroupName(String id, String newGroupName) throws KeycloakProvisioningException {
        GroupResource groupResource = keycloak.realm(targetRealm).groups().group(id);
        GroupRepresentation group = groupResource.toRepresentation();
        if (group == null) {
            log.warn("Group with ID {} not found", id);
            throw new KeycloakProvisioningException("Group not found for update");
        }

        group.setName(newGroupName);
        try {
            groupResource.update(group);
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == 409) {
                log.warn("Another group with name {} already exists", newGroupName);
                throw new KeycloakGroupNameConflictException(
                        "Another group with the same name already exists in Keycloak", e);
            } else {
                log.error("Failed to update group. Status: {}, Response: {}", e.getResponse().getStatus(),
                        e.getResponse().readEntity(String.class));
                throw new KeycloakProvisioningException("Failed to update group in Keycloak", e);
            }
        }
        log.info("Group with ID: {} updated to new name: {}", id, newGroupName);
        return id;
    }

    @Override
    public void deleteGroup(String groupId) throws KeycloakGroupNotFoundException, KeycloakProvisioningException {
        try {
            keycloak.realm(targetRealm).groups().group(groupId).remove();
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == 404) {
                log.warn("Group with ID {} not found", groupId);
                throw new KeycloakGroupNotFoundException("Group not found for delete");
            }
            log.error("Failed to delete group. Status: {}, Response: {}", e.getResponse().getStatus(),
                    e.getResponse().readEntity(String.class));
            throw new KeycloakProvisioningException("Failed to delete group in Keycloak", e);
        }

    }

    private static String generateRandomPassword() {
        String upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String specialCharacters = "!@#$%^&*()-_+=<>?";

        String combinedChars = upperCaseLetters + lowerCaseLetters + numbers + specialCharacters;
        ArrayList<Character> passwordChars = new ArrayList<>();

        passwordChars.add(upperCaseLetters.charAt((int) (Math.random() * upperCaseLetters.length())));
        passwordChars.add(lowerCaseLetters.charAt((int) (Math.random() * lowerCaseLetters.length())));
        passwordChars.add(numbers.charAt((int) (Math.random() * numbers.length())));
        passwordChars.add(specialCharacters.charAt((int) (Math.random() * specialCharacters.length())));

        for (int i = 4; i < 8; i++) {
            passwordChars.add(combinedChars.charAt((int) (Math.random() * combinedChars.length())));
        }

        Collections.shuffle(passwordChars);
        StringBuilder password = new StringBuilder();
        for (char c : passwordChars) {
            password.append(c);
        }
        return password.toString();

    }
}
