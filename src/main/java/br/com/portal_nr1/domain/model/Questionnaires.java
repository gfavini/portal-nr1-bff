package br.com.portal_nr1.domain.model;

import java.util.ArrayList;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Questionnaires {

    Questionnaire current;
    ArrayList<Questionnaire> versions;   
}