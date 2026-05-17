package br.com.portal_nr1.application.exception;

public class QuestionnaireNotFoundException extends RuntimeException {

	public QuestionnaireNotFoundException(String message) {
		super(message);
	}
}