package br.com.portal_nr1.infrastructure.config;

import static org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	@Value("${app.frontend.logout-redirect:http://localhost:4200/login}")
	String logoutRedirect;

	@Value("${app.frontend.login-redirect:http://localhost:4200/}")
	String loginRedirect;

	@Bean
	@Order(2)
	SecurityFilterChain securityFilterChain(HttpSecurity http,
			ClientRegistrationRepository clientRegistrationRepository,
			OAuth2AuthorizedClientRepository authorizedClientRepository) throws Exception {
		return http
				.cors(Customizer.withDefaults())
				.csrf(csrf -> csrf
						.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
				.sessionManagement(session -> session.sessionCreationPolicy(IF_REQUIRED))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(
								"/v3/api-docs/**",
								"/swagger-ui/**",
								"/swagger-ui.html",
								"/actuator/health",
								"/login",
								"/error")
						.permitAll()
						.requestMatchers("/api/admin/**").hasRole("ADMINISTRATOR")
						.requestMatchers("/api/respondent/**").hasRole("RESPONDENT")
						.requestMatchers("/api/**").authenticated()
						.anyRequest().permitAll())
				.formLogin(form -> form
                        .loginPage("/login")
                        .permitAll())
				.oauth2Login(oauth -> oauth
						.authorizationEndpoint(endpoint -> endpoint
								.baseUri(
										OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI))
						.loginPage("/oauth2/authorization/questionnaire-bff")
						.successHandler(oauth2SuccessHandler())
						.userInfoEndpoint(userInfo -> userInfo.userAuthoritiesMapper(rolesAuthoritiesMapper())))
				.oauth2Client(Customizer.withDefaults())
				.oauth2ResourceServer(
						oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
				.logout(logout -> logout
						.logoutRequestMatcher(PathPatternRequestMatcher.pathPattern(HttpMethod.GET, "/logout"))
						.invalidateHttpSession(true)
						.clearAuthentication(true)
						.logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository)))
				.exceptionHandling(ex -> ex
						.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
				.build();
	}

	@Bean
	OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler(ClientRegistrationRepository repository) {
		OidcClientInitiatedLogoutSuccessHandler handler = new OidcClientInitiatedLogoutSuccessHandler(repository);
		handler.setPostLogoutRedirectUri(logoutRedirect);
		return handler;
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SavedRequestAwareAuthenticationSuccessHandler oauth2SuccessHandler() {
		SavedRequestAwareAuthenticationSuccessHandler handler = new SavedRequestAwareAuthenticationSuccessHandler();
		handler.setDefaultTargetUrl(loginRedirect);
		handler.setAlwaysUseDefaultTargetUrl(false); // mantém redirect do Swagger quando há saved request
		return handler;
	}

	@Bean
	JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(jwt -> {
			Set<GrantedAuthority> auths = new HashSet<>();
			Object rolesClaim = jwt.getClaim("roles");
			if (rolesClaim instanceof Collection<?> roleList) {
				for (Object role : roleList) {
					if (role instanceof String r) {
						auths.add(new SimpleGrantedAuthority("ROLE_" + r.toUpperCase()));
					}
				}
			} else if (rolesClaim instanceof Map<?, ?> realm) {
				Object nestedRoles = realm.get("roles");
				if (nestedRoles instanceof Collection<?> roleList) {
					for (Object role : roleList) {
						if (role instanceof String r) {
							auths.add(new SimpleGrantedAuthority("ROLE_" + r.toUpperCase()));
						}
					}
				}
			}
			return auths;
		});
		return converter;
	}

	@Bean
	GrantedAuthoritiesMapper rolesAuthoritiesMapper() {
		return authorities -> {
			Set<GrantedAuthority> mapped = new HashSet<>();
			for (GrantedAuthority authority : authorities) {
				if(authority instanceof OidcUserAuthority oidc){
					// prefer ID token claims — custom claims (roles/groups) are added there by tokenCustomizer
					Map<String, Object> claims = oidc.getIdToken() != null
							? oidc.getIdToken().getClaims()
							: (oidc.getUserInfo() != null ? oidc.getUserInfo().getClaims() : Map.of());
					mapped.addAll(extractRoles(claims));
				} else if (authority instanceof OAuth2UserAuthority oauth) {
					mapped.addAll(extractRoles(oauth.getAttributes()));
				}
			} 
			return mapped;
		};
	}

	private Collection<GrantedAuthority> extractRoles(Map<String, Object> claims) {
		Set<GrantedAuthority> collected = new HashSet<>();
		Object roles = claims.get("roles");
		if (roles instanceof Collection<?> roleList) {
			for (Object role : roleList) {
				if (role instanceof String r) {
					collected.add(new SimpleGrantedAuthority("ROLE_" + r));
				}
			}
		}
		Object groups = claims.get("groups");
		if (groups instanceof Collection<?> groupList) {
			for (Object group : groupList) {
				if (group instanceof String g) {
					collected.add(new SimpleGrantedAuthority("GROUP_" + g));
				}
			}
		}
		return collected;
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(java.util.List.of("http://localhost:4200"));
		config.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(java.util.List.of("Authorization", "Cache-Control", "Content-Type", "X-XSRF-TOKEN"));
		config.setAllowCredentials(true);
		config.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
}