package br.com.portal_nr1.infrastructure.adapters.out.persistence;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import br.com.portal_nr1.application.ports.out.BookRepositoryPort;
import br.com.portal_nr1.domain.model.Book;

public class InMemoryBookRepository implements BookRepositoryPort {

	private final Map<String, Book> books = new ConcurrentHashMap<>();

	@Override
	public Book save(Book book) {
		books.put(book.getId(), book);
		return book;
	}
}