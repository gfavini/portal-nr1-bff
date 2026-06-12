package br.com.portal_nr1.infrastructure.security;

import java.security.Principal;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class AuthenticatedUserResolver {

	public AuthenticatedUser resolve(Principal principal, Authentication authentication) {
		log.debug("Auth type: {} | Authorities: {}", 
			authentication != null ? authentication.getClass().getSimpleName() : "null",
			authentication != null ? authentication.getAuthorities() : "none");
		
		String id = resolveId(authentication);
		List<String> roles = authoritiesByPrefix(authentication, "ROLE_");
		List<String> groups = authoritiesByPrefix(authentication, "GROUP_");
		String email = resolveEmail(principal, authentication);
		String username = resolveUsername(principal, authentication, email);

		log.debug("Resolved user: {} | id: {} | roles: {} | groups: {}", username, id, roles, groups);
		return new AuthenticatedUser(id, username, email, roles, groups);
	}

	private static List<String> authoritiesByPrefix(Authentication authentication, String prefix) {
		if (authentication == null || authentication.getAuthorities() == null) {
			return List.of();
		}

		List<String> roles = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.filter(authority -> authority != null && authority.startsWith(prefix))
				.map(authority -> authority.substring(prefix.length()))
				.distinct()
				.sorted()
				.toList();

		return roles;
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

	private static String resolveId(Authentication authentication) {
		if (authentication instanceof OAuth2AuthenticationToken oauth
				&& oauth.getPrincipal() instanceof DefaultOidcUser oidc) {
			String sub = oidc.getSubject();
			if (sub != null && !sub.isBlank()) {
				return sub;
			}

			Object claimId = oidc.getClaims().get("id");
			if (claimId instanceof String idClaim && !idClaim.isBlank()) {
				return idClaim;
			}

			Object claimSub = oidc.getClaims().get("sub");
			if (claimSub instanceof String subClaim && !subClaim.isBlank()) {
				return subClaim;
			}
		}

		if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
			var jwt = jwtAuthenticationToken.getToken();
			String id = jwt.getClaimAsString("id");
			if (id != null && !id.isBlank()) {
				return id;
			}

			String sub = jwt.getClaimAsString("sub");
			if (sub != null && !sub.isBlank()) {
				return sub;
			}
		}

		return "";
	}
}