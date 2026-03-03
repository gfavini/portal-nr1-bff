package br.com.portal_nr1.application.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class LogoutService {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final RestTemplate restTemplate;

    @Value("${keycloak.logout-uri:http://localhost:8081/realms/questionnaire-platform/protocol/openid-connect/logout}")
    private String keycloakLogoutUri;

    @Value("${keycloak.token-uri:http://localhost:8081/realms/questionnaire-platform/protocol/openid-connect/token}")
    private String keycloakTokenUri;

    @Value("${spring.security.oauth2.client.registration.questionnaire-bff.client-id:questionnaire-bff}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.questionnaire-bff.client-secret:#{null}}")
    private String clientSecret;

    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            // Revoke token in Keycloak
            if (authentication != null) {
                revokeTokenInKeycloak(authentication);
            }

            // Invalidate HTTP session
            request.getSession().invalidate();

            // Clear Spring Security context
            SecurityContextHolder.clearContext();

            log.info("User successfully logged out");
        } catch (Exception e) {
            log.error("Error during logout process", e);
            throw new RuntimeException("Logout failed", e);
        }
    }

    private void revokeTokenInKeycloak(Authentication authentication) {
        try {
            // OAuth2 Login (browser flow) - has refresh token
            if (authentication instanceof OAuth2AuthenticationToken oauth2Auth) {
                revokeOAuth2Token(oauth2Auth);
            } 
            // JWT Bearer token (direct API access) - revoke access token
            else if (authentication instanceof JwtAuthenticationToken jwtAuth) {
                revokeJwtToken(jwtAuth);
            }
            else {
                log.debug("Unknown authentication type: {}", authentication.getClass().getName());
            }
        } catch (Exception e) {
            log.warn("Error revoking token in Keycloak", e);
        }
    }

    private void revokeOAuth2Token(OAuth2AuthenticationToken oauth2Auth) {
        try {
            String registrationId = "questionnaire-bff";
            OAuth2AuthorizedClient authorizedClient = authorizedClientService
                    .loadAuthorizedClient(registrationId, oauth2Auth.getName());

            if (authorizedClient != null) {
                var refreshToken = authorizedClient.getRefreshToken();
                if (refreshToken != null) {
                    String refreshTokenValue = refreshToken.getTokenValue();
                    revokeToken(refreshTokenValue, "refresh_token");
                    log.debug("OAuth2 refresh token revoked in Keycloak");
                } else {
                    log.debug("No refresh token found for OAuth2 user");
                }
            } else {
                log.debug("No authorized client found for: {}", oauth2Auth.getName());
            }
        } catch (Exception e) {
            log.warn("Could not revoke OAuth2 token in Keycloak: {}", e.getMessage());
        }
    }

    private void revokeJwtToken(JwtAuthenticationToken jwtAuth) {
        try {
            String accessToken = jwtAuth.getToken().getTokenValue();
            revokeToken(accessToken, "access_token");
            log.debug("JWT access token revoked in Keycloak");
        } catch (Exception e) {
            log.warn("Could not revoke JWT token in Keycloak: {}", e.getMessage());
        }
    }

    private void revokeToken(String token, String tokenTypeHint) {
        try {
            // Keycloak revoke endpoint
            String revokeUri = keycloakTokenUri.replace("/token", "/revoke");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(clientId, clientSecret != null ? clientSecret : "");

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", clientId);
            body.add("token", token);
            body.add("token_type_hint", tokenTypeHint);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            
            ResponseEntity<String> res = restTemplate.postForEntity(revokeUri, request, String.class);
            log.debug("Token revoked successfully at Keycloak");
        } catch (Exception e) {
            log.warn("Could not revoke token in Keycloak: {}", e.getMessage());
            // Don't fail completely if Keycloak revocation fails
        }
    }
}
