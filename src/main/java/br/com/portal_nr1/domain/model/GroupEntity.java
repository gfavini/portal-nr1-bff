package br.com.portal_nr1.domain.model;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "groups")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class GroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "group_departments", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "department")
    @Builder.Default
    private Set<String> departments = new HashSet<>();

    @Column(nullable = true)
    private String assignedQuestionnaireId;

    @Column(nullable = true)
    private Integer assignedQuestionnaireVersion;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GroupStatus status;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = true)
    private Instant lastInviteSentAt;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private InviteScope lastInviteScope;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();
}
