package br.com.portal_nr1.infrastructure.adapters.in.web;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.portal_nr1.application.services.LogoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


@Log4j2
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BffController {

	private final LogoutService logoutService;

	@GetMapping("/me")
	@SecurityRequirement(name = "oauth2")
	@Operation(summary = "Dados do usuário autenticado")
	public Map<String, Object> me(Principal principal, Authentication authentication) {
		log.info("Authentication class: {}", authentication.getClass().getName());
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
	@SecurityRequirement(name = "oauth2")
	@Operation(summary = "Logout da aplicação e Keycloak")
	public Map<String, String> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		logoutService.logout(request, response, authentication);
		return Map.of("status", "ok", "message", "Logged out successfully");
	}

	private record UserInfo(String username, String email, List<String> roles, List<String> groups) {}


	private UserInfo extractUserInfo(Principal principal, Authentication authentication) {
		// Roles/groups must reflect what Spring Security is actually using for authorization.
		List<String> roles = authoritiesByPrefix(authentication, "ROLE_");
		List<String> groups = authoritiesByPrefix(authentication, "GROUP_");

		// Identity fields differ depending on authentication type. We normalize.
		String email = resolveEmail(principal, authentication);
		String username = resolveUsername(principal, authentication, email);

		return new UserInfo(username, email, roles, groups);
	}

	private static List<String> authoritiesByPrefix(Authentication authentication, String prefix) {
		if (authentication == null || authentication.getAuthorities() == null) {
			return List.of();
		}
		return authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.filter(a -> a != null && a.startsWith(prefix))
			.map(a -> a.substring(prefix.length()))
			.distinct()
			.sorted()
			.toList();
	}

	private static String resolveEmail(Principal principal, Authentication authentication) {
		// OIDC login session: easiest/most reliable.
		if (authentication instanceof OAuth2AuthenticationToken oauth && oauth.getPrincipal() instanceof DefaultOidcUser oidc) {
			String oidcEmail = oidc.getEmail();
			if (oidcEmail != null && !oidcEmail.isBlank()) {
				return oidcEmail;
			}
			Object claimEmail = oidc.getClaims().get("email");
			if (claimEmail instanceof String s && !s.isBlank()) {
				return s;
			}
			Object preferred = oidc.getClaims().get("preferred_username");
			if (preferred instanceof String s && s.contains("@")) {
				return s;
			}
		}

		// Swagger bearer token / resource server.
		if (authentication instanceof JwtAuthenticationToken jwtAuth) {
			var jwt = jwtAuth.getToken();
			String email = jwt.getClaimAsString("email");
			if (email != null && !email.isBlank()) {
				return email;
			}
			String preferred = jwt.getClaimAsString("preferred_username");
			if (preferred != null && preferred.contains("@")) {
				return preferred;
			}
		}

		// Fallback: principal name (may be sub/uuid).
		return principal != null ? principal.getName() : "";
	}

	private static String resolveUsername(Principal principal, Authentication authentication, String emailFallback) {
		// Prefer human-friendly username when present; otherwise use email; otherwise principal name.
		if (authentication instanceof OAuth2AuthenticationToken oauth && oauth.getPrincipal() instanceof DefaultOidcUser oidc) {
			Object preferred = oidc.getClaims().get("preferred_username");
			if (preferred instanceof String s && !s.isBlank()) {
				return s;
			}
		}

		if (authentication instanceof JwtAuthenticationToken jwtAuth) {
			var jwt = jwtAuth.getToken();
			String preferred = jwt.getClaimAsString("preferred_username");
			if (preferred != null && !preferred.isBlank()) {
				return preferred;
			}
		}

		if (emailFallback != null && !emailFallback.isBlank()) {
			return emailFallback;
		}

		return principal != null ? principal.getName() : "";
	}
}