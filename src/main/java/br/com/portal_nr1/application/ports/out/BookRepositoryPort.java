package br.com.portal_nr1.application.ports.out;

import br.com.portal_nr1.domain.model.Book;

public interface BookRepositoryPort {
	Book save(Book book);
}