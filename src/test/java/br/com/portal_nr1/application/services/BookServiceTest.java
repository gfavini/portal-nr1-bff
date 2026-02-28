package br.com.portal_nr1.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.portal_nr1.application.ports.out.BookRepositoryPort;
import br.com.portal_nr1.domain.model.Book;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

	@Mock
	private BookRepositoryPort bookRepositoryPort;

	@InjectMocks
	private BookService bookService;

	@Captor
	private ArgumentCaptor<Book> bookCaptor;

	@Test
	void shouldGenerateIdAndPersistBook() {
		when(bookRepositoryPort.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Book saved = bookService.createBook("Clean Code", "Robert C. Martin");

		verify(bookRepositoryPort).save(bookCaptor.capture());
		Book captured = bookCaptor.getValue();

		assertThat(saved).isEqualTo(captured);
		assertThat(saved.getId()).isNotBlank();
		assertThat(saved.getTitle()).isEqualTo("Clean Code");
		assertThat(saved.getAuthor()).isEqualTo("Robert C. Martin");
	}
}