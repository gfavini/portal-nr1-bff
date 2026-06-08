package br.com.portal_nr1.infrastructure.adapters.out.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.portal_nr1.domain.model.AppUser;


public interface AppUserRepository extends JpaRepository<AppUser, String>{
    Optional<AppUser> findByUsername(String username);
}
