package br.com.portal_nr1.domain.model;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Questionnaires {

    Questionnaire current;
    ArrayList<Questionnaire> versions;   
}