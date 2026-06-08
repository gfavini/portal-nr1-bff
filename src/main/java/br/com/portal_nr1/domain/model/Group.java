package br.com.portal_nr1.domain.model;

import java.time.Instant;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Group {

    private String id;

    private String name;
    private Integer respondentCount;
    private Set<String> departments;
    private String assignedQuestionnaireId;
    private Integer assignedQuestionnaireVersion;
    private GroupStatus status;
    private Instant expiresAt;
    private Instant lastInviteSentAt;
    private InviteScope lastInviteScope;
    private Instant createdAt;
    private Instant updatedAt;
}
