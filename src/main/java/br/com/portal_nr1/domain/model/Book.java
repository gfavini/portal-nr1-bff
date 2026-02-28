package br.com.portal_nr1.domain.model;

import java.util.Objects;

/**
 * Domain model representing a book without framework dependencies.
 */
public class Book {

	private final String id;
	private final String title;
	private final String author;

	public Book(String id, String title, String author) {
		this.id = Objects.requireNonNull(id, "id is required");
		this.title = Objects.requireNonNull(title, "title is required");
		this.author = Objects.requireNonNull(author, "author is required");
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Book)) {
			return false;
		}
		Book book = (Book) o;
		return id.equals(book.id) && title.equals(book.title) && author.equals(book.author);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, title, author);
	}

	@Override
	public String toString() {
		return "Book{" +
			"id='" + id + '\'' +
			", title='" + title + '\'' +
			", author='" + author + '\'' +
			'}';
	}
}