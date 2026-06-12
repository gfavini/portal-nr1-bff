package br.com.portal_nr1.infrastructure.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import br.com.portal_nr1.infrastructure.adapters.out.persistence.AppUserRepository;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class TokenCustomizerConfig {
    private final AppUserRepository userRepository;

    @Bean
    OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return context -> {
            String username = context.getPrincipal().getName();
            userRepository.findByUsername(username).ifPresent(user -> {
                List<String> roles = List.copyOf(user.getRoles());
                List<String> groups = (user.getGroup() != null && user.getGroup().getId() != null)
                        ? List.of(user.getGroup().getId())
                        : List.of();

                context.getClaims()
                        .claim("sub", user.getId())
                        .claim("id", user.getId())
                        .claim("roles", roles)
                        .claim("groups", groups)
                        .claim("email", user.getEmail())
                        .claim("preferred_username", user.getUsername());
            });
        };
    }
}
