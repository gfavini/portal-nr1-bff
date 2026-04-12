package br.com.portal_nr1.infrastructure.security;

import java.security.Principal;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserResolver {

	public AuthenticatedUser resolve(Principal principal, Authentication authentication) {
		List<String> roles = authoritiesByPrefix(authentication, "ROLE_");
		List<String> groups = authoritiesByPrefix(authentication, "GROUP_");
		String email = resolveEmail(principal, authentication);
		String username = resolveUsername(principal, authentication, email);

		return new AuthenticatedUser(username, email, roles, groups);
	}

	private static List<String> authoritiesByPrefix(Authentication authentication, String prefix) {
		if (authentication == null || authentication.getAuthorities() == null) {
			return List.of();
		}

		return authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.filter(authority -> authority != null && authority.startsWith(prefix))
				.map(authority -> authority.substring(prefix.length()))
				.distinct()
				.sorted()
				.toList();
	}

	private static String resolveEmail(Principal principal, Authentication authentication) {
		if (authentication instanceof OAuth2AuthenticationToken oauth
				&& oauth.getPrincipal() instanceof DefaultOidcUser oidc) {
			String oidcEmail = oidc.getEmail();
			if (oidcEmail != null && !oidcEmail.isBlank()) {
				return oidcEmail;
			}

			Object claimEmail = oidc.getClaims().get("email");
			if (claimEmail instanceof String email && !email.isBlank()) {
				return email;
			}

			Object preferred = oidc.getClaims().get("preferred_username");
			if (preferred instanceof String preferredUsername && preferredUsername.contains("@")) {
				return preferredUsername;
			}
		}

		if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
			var jwt = jwtAuthenticationToken.getToken();
			String email = jwt.getClaimAsString("email");
			if (email != null && !email.isBlank()) {
				return email;
			}

			String preferred = jwt.getClaimAsString("preferred_username");
			if (preferred != null && preferred.contains("@")) {
				return preferred;
			}
		}

		return principal != null ? principal.getName() : "";
	}

	private static String resolveUsername(Principal principal, Authentication authentication, String emailFallback) {
		if (authentication instanceof OAuth2AuthenticationToken oauth
				&& oauth.getPrincipal() instanceof DefaultOidcUser oidc) {
			Object preferred = oidc.getClaims().get("preferred_username");
			if (preferred instanceof String preferredUsername && !preferredUsername.isBlank()) {
				return preferredUsername;
			}
		}

		if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
			var jwt = jwtAuthenticationToken.getToken();
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