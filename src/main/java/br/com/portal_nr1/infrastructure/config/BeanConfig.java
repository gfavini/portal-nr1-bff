package br.com.portal_nr1.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.portal_nr1.application.ports.in.CreateBookUseCase;
import br.com.portal_nr1.application.ports.out.BookRepositoryPort;
import br.com.portal_nr1.application.services.BookService;
import br.com.portal_nr1.infrastructure.adapters.out.persistence.InMemoryBookRepository;

@Configuration
public class BeanConfig {

	@Bean
	BookRepositoryPort bookRepository() {
		return new InMemoryBookRepository();
	}

	@Bean
	CreateBookUseCase createBookUseCase(BookRepositoryPort bookRepositoryPort) {
		return new BookService(bookRepositoryPort);
	}
}