package br.com.portal_nr1.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;

@Configuration
@OpenAPIDefinition(
	info = @Info(title = "Portal NR1 API", version = "v1", description = "BFF protegido por Keycloak (Authorization Code)"),
	security = {@SecurityRequirement(name = "oauth2")}
)
@SecurityScheme(
	name = "oauth2",
	type = SecuritySchemeType.OAUTH2,
	in = SecuritySchemeIn.HEADER,
	flows = @OAuthFlows(
		authorizationCode = @OAuthFlow(
			authorizationUrl = "${KEYCLOAK_AUTH_URL:http://localhost:8081/realms/questionnaire-platform/protocol/openid-connect/auth}",
			tokenUrl = "${KEYCLOAK_TOKEN_URI:http://localhost:8081/realms/questionnaire-platform/protocol/openid-connect/token}",
			scopes = {
				@OAuthScope(name = "openid", description = "OpenID scope"),
				@OAuthScope(name = "profile", description = "Profile scope"),
				@OAuthScope(name = "email", description = "Email scope"),
				@OAuthScope(name = "roles", description = "Realm roles"),
				@OAuthScope(name = "offline_access", description = "Refresh tokens")
			}
		)
	)
)
public class OpenApiConfig {

	@Bean
	OpenAPI baseOpenAPI(@Value("${keycloak.auth-server-url:http://localhost:8081}") String authServerUrl) {
		return new OpenAPI()
			.info(new io.swagger.v3.oas.models.info.Info()
				.title("Portal NR1 API")
				.version("v1")
				.description("Backend for Frontend com OAuth2 Authorization Code")
				.license(new License().name("Apache 2.0")))
			.externalDocs(new ExternalDocumentation()
				.description("Keycloak")
				.url(authServerUrl));
	}
}