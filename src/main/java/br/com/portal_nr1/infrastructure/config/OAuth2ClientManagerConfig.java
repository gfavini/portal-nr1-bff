package br.com.portal_nr1.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

@Configuration
public class OAuth2ClientManagerConfig {

	@Bean
	OAuth2AuthorizedClientService authorizedClientService(ClientRegistrationRepository registrations) {
		return new InMemoryOAuth2AuthorizedClientService(registrations);
	}

	@Bean
	OAuth2AuthorizedClientRepository authorizedClientRepository(OAuth2AuthorizedClientService service) {
		return new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(service);
	}

	@Bean
	OAuth2AuthorizedClientManager authorizedClientManager(ClientRegistrationRepository clientRegistrationRepository,
			OAuth2AuthorizedClientRepository authorizedClientRepository,
			OAuth2AuthorizedClientService authorizedClientService) {
		OAuth2AuthorizedClientProvider provider = OAuth2AuthorizedClientProviderBuilder.builder()
			.authorizationCode()
			.refreshToken()
			.build();

		AuthorizedClientServiceOAuth2AuthorizedClientManager manager =
			new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientService);
		manager.setAuthorizedClientProvider(provider);
		return manager;
	}
}