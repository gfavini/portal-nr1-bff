package br.com.portal_nr1.application.ports.out;

import java.util.List;
import java.util.Map;

import br.com.portal_nr1.domain.model.Respondent;

public interface RespondentRepositoryPort {

    Respondent save(Respondent respToSave);

    Respondent getGroupByEmail(String email);

    List<Respondent> findByFilters(String groupId, String firstName, String lastName, String department);

    Respondent findById(String id);

    Map<String, Integer> countByGroupIds();

    int countByGroupId(String id);

    
} 