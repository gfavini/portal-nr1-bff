package br.com.portal_nr1.infrastructure.adapters.in.web.dto;

import java.time.Instant;
import java.util.ArrayList;

public record QuestionnaireResponseItem(
    String id,
    Integer version,
    Instant publishedAt,
    String createdBy,
    String title,
    String description,
    ArrayList<QuestionResponseItem> questions

) {

}
