package br.com.portal_nr1.infrastructure.adapters.in.web.dto;

import java.util.List;

public record RespondentListResponse (
    List<RespondentResponseItem> respondents,
    Integer total
){

}
