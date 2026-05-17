package br.com.portal_nr1.infrastructure.adapters.out.persistence;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import br.com.portal_nr1.application.exception.QuestionnaireNotFoundInRepoException;
import br.com.portal_nr1.application.exception.QuestionnaireRepositoryException;
import br.com.portal_nr1.application.ports.out.QuestionnaireRespositoryPort;
import br.com.portal_nr1.domain.model.Questionnaire;

@Repository
public class InMemoryQuestionnaireRespository implements QuestionnaireRespositoryPort {

	private final Map<String, Questionnaire> questionnaires = new ConcurrentHashMap<>();

	@Override
	public ArrayList<Questionnaire> fetchAll() {
		return questionnaires.entrySet().stream()
			.filter(entry -> !entry.getKey().equals("QUEST#LATEST"))
			.map(entry -> entry.getValue())
			.sorted((q1, q2) -> Long.compare(q2.getPublishedAt().longValue(), q1.getPublishedAt().longValue()))
			.collect(java.util.stream.Collectors.toCollection(ArrayList::new));
	}
	
	@Override
	public Questionnaire fetchLatest() throws QuestionnaireNotFoundInRepoException{
		Questionnaire latest = questionnaires.get("QUEST#LATEST");
		if (latest == null) {
			throw new QuestionnaireNotFoundInRepoException("No questionnaire available.");
		}
		return latest;
	}

	@Override
	public Questionnaire fetch(String id) throws QuestionnaireNotFoundInRepoException {
		Questionnaire questionnaire = this.fetchAll().stream()
			.filter(q -> q.getId().equals(id))
			.findFirst()
			.orElse(null);
		if(questionnaire == null) {
			throw new QuestionnaireNotFoundInRepoException("Questionnaire with id " + id + " not found.");
		}
		return questionnaire;
	}

	@Override
	public Questionnaire fetchByVersion(Integer version) throws QuestionnaireNotFoundInRepoException {
		Questionnaire questionnaire = this.fetchAll().stream()
			.filter(q -> q.getVersion().equals(version))
			.findFirst()
			.orElse(null);
		if(questionnaire == null) {
			throw new QuestionnaireNotFoundInRepoException("Questionnaire with version " + version + " not found.");
		}
		return questionnaire;
	}

	@Override
	public Questionnaire saveAndUpdateLatest(Questionnaire newQuestionnaire) {
		try {
			this.fetchLatest();
		} catch (QuestionnaireNotFoundInRepoException e) {
			// Se não houver um questionário existente, podemos criar o primeiro sem precisar atualizar a versão
			Questionnaire firstQuestionnaire = new Questionnaire(
				newQuestionnaire.getId() != null ? newQuestionnaire.getId() : java.util.UUID.randomUUID().toString(),
				1,
				newQuestionnaire.getPublishedAt() != null ? newQuestionnaire.getPublishedAt() : java.time.Instant.now().toEpochMilli(),
				newQuestionnaire.getCreatedBy(),
				newQuestionnaire.getTitle(),
				newQuestionnaire.getDescription(),
				newQuestionnaire.getQuestions()
			);
			questionnaires.put(firstQuestionnaire.getId(), firstQuestionnaire);
			questionnaires.put("QUEST#LATEST", firstQuestionnaire);
			return firstQuestionnaire;
		}
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