package br.com.portal_nr1.infrastructure.adapters.in.web.mapper;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

import br.com.portal_nr1.domain.model.Question;
import br.com.portal_nr1.domain.model.Questionnaire;
import br.com.portal_nr1.domain.model.Questionnaires;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.QuestionResponseItem;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.QuestionnaireRequestItem;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.QuestionnaireResponse;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.QuestionnaireResponseItem;

public final class QuestionnaireMapper {

	private QuestionnaireMapper() {
	}

	public static QuestionnaireResponse toResponse(Questionnaires questionnaires) {
		if (questionnaires == null) {
			return new QuestionnaireResponse(null, new ArrayList<>());
		}

		QuestionnaireResponseItem current = toItem(questionnaires.getCurrent());
		ArrayList<QuestionnaireResponseItem> versions = toItems(questionnaires.getVersions());
		return new QuestionnaireResponse(current, versions);
	}

	public static QuestionnaireResponseItem toResponse(Questionnaire questionnaire) {
		return toItem(questionnaire);
	}

	public static Questionnaire toDomain(QuestionnaireRequestItem request) {
		if (request == null) {
			return null;
		}

		ArrayList<Question> questions = toQuestionModels(request.questions());

		return Questionnaire.builder()
				.title(request.title())
				.description(request.description())
				.questions(questions)
				.build();
	}

	private static ArrayList<Question> toQuestionModels(ArrayList<QuestionResponseItem> questions) {
		if (questions == null || questions.isEmpty()) {
			return new ArrayList<>();
		}

		return questions.stream()
				.filter(Objects::nonNull)
				.map(QuestionnaireMapper::toQuestionModel)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private static Question toQuestionModel(QuestionResponseItem question) {
		if (question == null) {
			return null;
		}

		return Question.builder()
				.id(question.id())
				.affirmation(question.affirmation())
				.pillar(question.pillar())
				.subPillar(question.subPillar())
				.required(question.required())
				.build();
	}
	

	private static ArrayList<QuestionnaireResponseItem> toItems(ArrayList<Questionnaire> questionnaires) {
		if (questionnaires == null || questionnaires.isEmpty()) {
			return new ArrayList<>();
		}

		return questionnaires.stream()
				.filter(Objects::nonNull)
				.map(QuestionnaireMapper::toItem)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private static QuestionnaireResponseItem toItem(Questionnaire questionnaire) {
		if (questionnaire == null) {
			return null;
		}

		Instant publishedAt = parseInstantOrNull(questionnaire.getPublishedAt());
		ArrayList<QuestionResponseItem> questions = toQuestionItems(questionnaire.getQuestions());

		return new QuestionnaireResponseItem(
				questionnaire.getId(),
				questionnaire.getVersion(),
				publishedAt,
				questionnaire.getCreatedBy(),
				questionnaire.getTitle(),
				questionnaire.getDescription(),
				questions
		);
	}

	private static ArrayList<QuestionResponseItem> toQuestionItems(ArrayList<Question> questions) {
		if (questions == null || questions.isEmpty()) {
			return new ArrayList<>();
		}

		return questions.stream()
				.filter(Objects::nonNull)
				.map(QuestionnaireMapper::toQuestionItem)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private static QuestionResponseItem toQuestionItem(Question question) {
		return new QuestionResponseItem(
				question.getId(),
				question.getAffirmation(),
				question.getPillar(),
				question.getSubPillar(),
				question.getRequired()
		);
	}

	private static Instant parseInstantOrNull(Number value) {
		if (value == null) {
			return null;
		}

		try {
			return Instant.ofEpochMilli(value.longValue());
		} catch (DateTimeParseException ignored) {
			return null;
		}
	}
}
