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

                context.getClaims()
                        .claim("roles", roles)
                        .claim("group_id", user.getGroup().getId())
                        .claim("group_name", user.getGroup().getName())
                        .claim("email", user.getEmail())
                        .claim("preferred_username", user.getUsername());
            });
        };
    }
}
