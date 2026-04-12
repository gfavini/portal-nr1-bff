package br.com.portal_nr1.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
public class Question {
    @Id
    String id;
    String affirmation;
    String pillar;
    String subPillar;
    Boolean required;
}
