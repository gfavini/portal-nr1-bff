package br.com.portal_nr1.domain.model;

import java.util.ArrayList;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
@AllArgsConstructor
public class Questionnaire {
    @Id
    String id;
    Integer version;
    Number publishedAt;
    String createdBy;
    String title;
    String description;
    ArrayList<Question> questions;
}
