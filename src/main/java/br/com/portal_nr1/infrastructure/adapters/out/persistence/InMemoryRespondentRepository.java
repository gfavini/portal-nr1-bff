package br.com.portal_nr1.infrastructure.adapters.out.persistence;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import br.com.portal_nr1.application.ports.out.RespondentRepositoryPort;
import br.com.portal_nr1.domain.model.Respondent;

public class InMemoryRespondentRepository implements RespondentRepositoryPort {
    private final Map<String, Respondent> respondents = new ConcurrentHashMap<>();

    @Override
    public Respondent save(Respondent respToSave) {
        this.respondents.put(respToSave.getId(), respToSave);
        return respToSave;
    }

    @Override
    public Respondent getGroupByEmail(String email) {
        return respondents.values().stream()
        .filter(resp -> resp.getEmail().equalsIgnoreCase(email))
        .findAny().orElse(null);
    }
}
