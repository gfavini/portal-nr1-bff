package br.com.portal_nr1.application.exception;

public class GroupAlreadyExistsException extends RuntimeException {

	public GroupAlreadyExistsException(String message) {
		super(message);
	}

	public GroupAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}
}