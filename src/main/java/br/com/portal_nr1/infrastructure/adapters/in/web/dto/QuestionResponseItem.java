package br.com.portal_nr1.infrastructure.adapters.in.web.dto;

public record QuestionResponseItem(
    String id,
    String affirmation,
    String pillar,
    String subPillar,
    Boolean required
) {}
