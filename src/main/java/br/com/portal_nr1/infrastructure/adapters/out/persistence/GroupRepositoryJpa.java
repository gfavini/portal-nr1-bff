package br.com.portal_nr1.infrastructure.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.portal_nr1.domain.model.GroupEntity;

public interface GroupRepositoryJpa extends JpaRepository<GroupEntity, String> {
}
