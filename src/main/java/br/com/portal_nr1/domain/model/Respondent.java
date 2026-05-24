package br.com.portal_nr1.domain.model;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Respondent{
        private String id;
        private String firstName;
        private String lastName;
        private String email;
        private Integer age;
        private String department;
        private Gender gender;
        private String groupId;
        private String groupName;
        private Instant createdAt;
        private String firstNameLower;
        private String lastNameLower;
    

        public static Respondent copy(Respondent toCopy){
            return new Respondent(toCopy.getId(), toCopy.getFirstName(), toCopy.getLastName(),
            toCopy.getEmail(), toCopy.getAge(), toCopy.getDepartment(), toCopy.getGender(),
            toCopy.getGroupId(), toCopy.getGroupName(),toCopy.getCreatedAt(),
            toCopy.getFirstNameLower(),toCopy.getLastNameLower());
        }
    }
