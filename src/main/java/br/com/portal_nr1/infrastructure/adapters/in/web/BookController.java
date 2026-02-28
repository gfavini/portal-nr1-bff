package br.com.portal_nr1.infrastructure.adapters.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.portal_nr1.application.ports.in.CreateBookUseCase;
import br.com.portal_nr1.domain.model.Book;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.BookRequest;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.BookResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/books")
public class BookController {

	private final CreateBookUseCase createBookUseCase;

	public BookController(CreateBookUseCase createBookUseCase) {
		this.createBookUseCase = createBookUseCase;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BookResponse createBook(@Valid @RequestBody BookRequest request) {
		Book book = createBookUseCase.createBook(request.title(), request.author());
		return new BookResponse(book.getId(), book.getTitle(), book.getAuthor());
	}
}