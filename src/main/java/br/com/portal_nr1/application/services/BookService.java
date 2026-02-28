package br.com.portal_nr1.application.services;

import java.util.Objects;
import java.util.UUID;

import br.com.portal_nr1.application.ports.in.CreateBookUseCase;
import br.com.portal_nr1.application.ports.out.BookRepositoryPort;
import br.com.portal_nr1.domain.model.Book;

public class BookService implements CreateBookUseCase {

	private final BookRepositoryPort bookRepository;

	public BookService(BookRepositoryPort bookRepository) {
		this.bookRepository = Objects.requireNonNull(bookRepository);
	}

	@Override
	public Book createBook(String title, String author) {
		String id = UUID.randomUUID().toString();
		Book book = new Book(id, title, author);
		return bookRepository.save(book);
	}
}