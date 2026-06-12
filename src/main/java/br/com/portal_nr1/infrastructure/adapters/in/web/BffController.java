package br.com.portal_nr1.infrastructure.adapters.in.web;

import java.security.Principal;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.portal_nr1.infrastructure.security.AuthenticatedUser;
import br.com.portal_nr1.infrastructure.security.AuthenticatedUserResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


@Log4j2
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BffController {

	private final AuthenticatedUserResolver authenticatedUserResolver;

	@GetMapping("/me")
	@SecurityRequirement(name = "oauth2")
	@Operation(summary = "Dados do usuário autenticado")
	public Map<String, Object> me(Principal principal, Authentication authentication) {
		log.info("Authentication class: {}", authentication.getClass().getName());
		AuthenticatedUser info = authenticatedUserResolver.resolve(principal, authentication);
		return Map.of("user", Map.of(
			"id", info.id(),
			"username", info.username(),
			"email", info.email(),
			"roles", info.roles(),
			"groups", info.groups()
		));
	}

	@GetMapping("/admin/test")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Endpoint protegido por ADMIN")
	public Map<String, String> admin() {
		return Map.of("status", "ok", "message", "ADMIN access granted");
	}

	@GetMapping("/respondent/test")
	@PreAuthorize("hasRole('RESPONDENT')")
	@Operation(summary = "Endpoint protegido por RESPONDENT")
	public Map<String, String> respondent() {
		return Map.of("status", "ok", "message", "RESPONDENT access granted");
	}

}