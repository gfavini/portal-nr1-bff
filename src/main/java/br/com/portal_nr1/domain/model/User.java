package br.com.portal_nr1.domain.model;

import java.util.List;

public record User(String username, String email, List<String> roles, List<String> groups) {
}