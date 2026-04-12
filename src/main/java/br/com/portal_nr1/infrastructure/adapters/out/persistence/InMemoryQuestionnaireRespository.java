package br.com.portal_nr1.infrastructure.adapters.out.persistence;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import br.com.portal_nr1.application.ports.out.QuestionnaireRespositoryPort;
import br.com.portal_nr1.domain.model.Question;
import br.com.portal_nr1.domain.model.Questionnaire;

@Repository
public class InMemoryQuestionnaireRespository implements QuestionnaireRespositoryPort {

	private final Map<String, Questionnaire> questionnaires = new ConcurrentHashMap<>();

	@Override
	public ArrayList<Questionnaire> fetchAll() {
		return questionnaires.values().stream()
			.sorted((q1, q2) -> Long.compare(q2.getPublishedAt().longValue(), q1.getPublishedAt().longValue()))
			.collect(java.util.stream.Collectors.toCollection(ArrayList::new));
	}
	
	@Override
	public Questionnaire fetchLatest() {
		return questionnaires.get("QUEST#LATEST");
	}

	@Override
	public Questionnaire fetch(String id) {
		return this.fetchAll().stream()
			.filter(q -> q.getId().equals(id))
			.findFirst()
			.orElse(null);
	}

	@Override
	public Questionnaire fetchByVersion(String version) {
		return this.fetchAll().stream()
			.filter(q -> q.getVersion().equals(version))
			.findFirst()
			.orElse(null);
	}

	@Override
	public Questionnaire saveAndUpdateLatest(Questionnaire newQuestionnaire) {
		Questionnaire latest = this.fetchLatest();
		Integer newVersion = (latest != null) ? latest.getVersion() + 1 : 1;
		String ID = newQuestionnaire.getId() != null ? newQuestionnaire.getId() : java.util.UUID.randomUUID().toString();
		Number publishedAt = newQuestionnaire.getPublishedAt() != null ? 
				newQuestionnaire.getPublishedAt() : java.time.Instant.now().toEpochMilli();

		Questionnaire updatedQuestionnaire = new Questionnaire(
			ID,
			newVersion,
			publishedAt,
			newQuestionnaire.getCreatedBy(),
			newQuestionnaire.getTitle(),
			newQuestionnaire.getDescription(),
			newQuestionnaire.getQuestions()
		);
		questionnaires.put(ID, updatedQuestionnaire);
		questionnaires.put("QUEST#LATEST", updatedQuestionnaire);
		return updatedQuestionnaire;
	}
}