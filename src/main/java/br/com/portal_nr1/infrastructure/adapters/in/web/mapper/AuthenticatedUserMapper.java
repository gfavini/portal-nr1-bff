package br.com.portal_nr1.infrastructure.adapters.in.web.mapper;
import br.com.portal_nr1.domain.model.User;
import br.com.portal_nr1.infrastructure.security.AuthenticatedUser;

public final class AuthenticatedUserMapper {
    private AuthenticatedUserMapper() {}

    public static User toDomain(AuthenticatedUser user) {
        return new User(user.username(), user.email(), user.roles(), user.groups());
    }
}