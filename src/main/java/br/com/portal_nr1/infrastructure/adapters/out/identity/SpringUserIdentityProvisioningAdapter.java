package br.com.portal_nr1.infrastructure.adapters.out.identity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.portal_nr1.application.ports.out.UserIdentityProvisioningPort;
import br.com.portal_nr1.domain.model.AppUser;
import br.com.portal_nr1.domain.model.ProvisionedRespondent;
import br.com.portal_nr1.domain.model.Respondent;
import br.com.portal_nr1.infrastructure.adapters.exception.UserEmailConflictException;
import br.com.portal_nr1.infrastructure.adapters.out.persistence.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


@Service
@RequiredArgsConstructor
@Log4j2
public class SpringUserIdentityProvisioningAdapter implements UserIdentityProvisioningPort{

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ProvisionedRespondent provisionRespondent(Respondent respondent) {
        if(userRepository.findByUsername(respondent.getEmail()).isPresent()) {
            log.warn("User with email {} already exists", respondent.getEmail());
            throw new UserEmailConflictException("A user with the same email already exists");
        }

        String tempPassword = generateRandomPassword();

        AppUser user = new AppUser();
        user.setUsername(respondent.getEmail());
        user.setEmail(respondent.getEmail());
        user.setFirstName(respondent.getFirstName());
        user.setLastName(respondent.getLastName());
        user.setPassword(passwordEncoder.encode(tempPassword));
        user.setEnabled(true);
        user.setRoles(Set.of("RESPONDENT"));

        AppUser saved = userRepository.save(user);
        log.info("User created with ID: {}", saved.getId());

        return new ProvisionedRespondent(saved.getId(), saved.getEmail(), tempPassword);
    }

    @Override
    public String provisionGroup(String groupName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'provisionGroup'");
    }

    @Override
    public void assignRespondentToGroup(String groupId, ProvisionedRespondent respondent) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'assignRespondentToGroup'");
    }

    @Override
    public ProvisionedRespondent provisionRespondentAndAssignGroup(Respondent respondent, String groupId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'provisionRespondentAndAssignGroup'");
    }

    @Override
    public String updateGroupName(String id, String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateGroupName'");
    }

    @Override
    public void deleteGroup(String groupId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteGroup'");
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
