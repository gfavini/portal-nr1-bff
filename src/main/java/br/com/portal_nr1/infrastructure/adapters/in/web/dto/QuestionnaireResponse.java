package br.com.portal_nr1.infrastructure.adapters.in.web.dto;

import java.util.ArrayList;

public record QuestionnaireResponse(
    QuestionnaireResponseItem current,
    ArrayList<QuestionnaireResponseItem> versions
) {

}
