package br.com.portal_nr1.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Respondent extends AppUser{
        private Integer age;
        private String department;
        private Gender gender;
        private String firstNameLower;
        private String lastNameLower;
    

        // public static Respondent copy(Respondent toCopy){
        //     return new Respondent(toCopy.getId(), toCopy.getFirstName(), toCopy.getLastName(),
        //     toCopy.getEmail(), toCopy.getAge(), toCopy.getDepartment(), toCopy.getGender(),toCopy.getGroup(),toCopy.getCreatedAt(),
        //     toCopy.getFirstNameLower(),toCopy.getLastNameLower());
        // }
    }
