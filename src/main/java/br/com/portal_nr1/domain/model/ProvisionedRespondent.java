package br.com.portal_nr1.domain.model;

public record ProvisionedRespondent(
    String id,
    String email,
    String temporaryPassword
) {
}
