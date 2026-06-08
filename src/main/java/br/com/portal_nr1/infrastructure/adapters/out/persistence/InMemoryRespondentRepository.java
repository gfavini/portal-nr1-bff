package br.com.portal_nr1.infrastructure.adapters.out.persistence;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import br.com.portal_nr1.application.ports.out.RespondentRepositoryPort;
import br.com.portal_nr1.domain.model.Respondent;

@Repository
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

    @Override
    public List<Respondent> findByFilters(String groupId, String firstName, String lastName, String department) {
        return respondents.values().stream()
        .filter(resp -> resp.getGroup().getId().equals(groupId))
        .filter(resp -> {if(firstName == null) return true;return resp.getFirstName().equalsIgnoreCase(firstName);})
        .filter(resp -> {if(lastName == null) return true;return resp.getLastName().equalsIgnoreCase(lastName);})
        .filter(resp -> {if(department == null) return true;return resp.getDepartment().equalsIgnoreCase(department);})
        .toList();
    }

    @Override
    public Respondent findById(String id) {
        return this.respondents.get(id);
    }

    @Override
    public Map<String, Integer> countByGroupIds() {
        Map<String, Integer> result =  respondents.values().stream().collect(
            Collectors.groupingBy(
                resp -> resp.getGroup().getId(), Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
            )
        );

        // Gambiarra para voltar 0 se o grupo ainda nao tiver respondente;
        respondents.values().stream().map(resp -> resp.getGroup().getId()).forEach(id -> result.putIfAbsent(id, 0));
        return result;
    }

    @Override
    public int countByGroupId(String id) {
        return respondents.values().stream()
        .filter(res -> res.getGroup().getId().equals(id))
        .collect(Collectors.collectingAndThen(Collectors.counting(),Long::intValue));
    }
}
