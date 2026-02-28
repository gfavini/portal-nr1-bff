package br.com.portal_nr1.application.ports.in;

import br.com.portal_nr1.domain.model.Book;

public interface CreateBookUseCase {
	Book createBook(String title, String author);
}