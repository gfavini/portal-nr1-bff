package br.com.portal_nr1.infrastructure.security;

import java.util.List;

public record AuthenticatedUser(String id, String username, String email, List<String> roles, List<String> groups) {
}