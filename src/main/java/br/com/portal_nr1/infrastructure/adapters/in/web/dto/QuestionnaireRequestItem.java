package br.com.portal_nr1.infrastructure.adapters.in.web.dto;

import java.util.ArrayList;

public record QuestionnaireRequestItem(
    String title,
    String description,
    ArrayList<QuestionResponseItem> questions
) {
}