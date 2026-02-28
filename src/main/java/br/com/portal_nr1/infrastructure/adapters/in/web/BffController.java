package br.com.portal_nr1.infrastructure.adapters.in.web;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api")
public class BffController {

	@GetMapping("/me")
	@SecurityRequirement(name = "oauth2")
	@Operation(summary = "Dados do usuário autenticado")
	public Map<String, Object> me(Principal principal, Authentication authentication) {
		var info = extractUserInfo(principal, authentication);
		return Map.of("user", Map.of(
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

	@PostMapping("/logout")
	@Operation(summary = "Logout da aplicação e Keycloak")
	public Map<String, String> logout() {
		// Real logout handled by Spring Security filter. Endpoint kept for discoverability.
		return Map.of("status", "ok", "message", "Logout initiated");
	}

	private record UserInfo(String username, String email, List<String> roles, List<String> groups) {}

	private UserInfo extractUserInfo(Principal principal, Authentication authentication) {
		if (authentication instanceof OAuth2AuthenticationToken oauth && oauth.getPrincipal() instanceof DefaultOidcUser oidc) {
			return new UserInfo(
				principal.getName(),
				oidc.getEmail(),
				toListByPrefix(oidc, "ROLE_"),
				toListByPrefix(oidc, "GROUP_")
			);
		}
		if (authentication instanceof JwtAuthenticationToken jwtAuth) {
			var jwt = jwtAuth.getToken();
			List<String> roles = jwtAuth.getAuthorities().stream()
				.map(a -> a.getAuthority())
				.filter(a -> a.startsWith("ROLE_"))
				.map(a -> a.substring("ROLE_".length()))
				.toList();
			List<String> groups = jwtAuth.getAuthorities().stream()
				.map(a -> a.getAuthority())
				.filter(a -> a.startsWith("GROUP_"))
				.map(a -> a.substring("GROUP_".length()))
				.toList();
			String email = Optional.ofNullable(jwt.getClaimAsString("email"))
				.orElse(jwt.getClaimAsString("preferred_username"));
			String username = Optional.ofNullable(jwt.getClaimAsString("preferred_username"))
				.orElse(jwt.getSubject());
			return new UserInfo(username, email, roles, groups);
		}
		throw new IllegalStateException("User is not authenticated");
	}

	private List<String> toListByPrefix(DefaultOidcUser oidc, String prefix) {
		return oidc.getAuthorities().stream()
			.map(auth -> auth.getAuthority())
			.filter(a -> a.startsWith(prefix))
			.map(a -> a.substring(prefix.length()))
			.toList();
	}
}