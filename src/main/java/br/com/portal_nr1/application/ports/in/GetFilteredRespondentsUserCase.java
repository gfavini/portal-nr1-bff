package br.com.portal_nr1.application.ports.in;

import java.util.List;

import br.com.portal_nr1.domain.model.Respondent;

public interface GetFilteredRespondentsUserCase {
    public List<Respondent> fetch(String groupId, String firstName, String lastName, String department);
}
