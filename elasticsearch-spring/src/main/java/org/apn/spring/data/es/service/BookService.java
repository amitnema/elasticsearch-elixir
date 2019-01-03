package org.apn.spring.data.es.service;

import java.util.Optional;

import org.apn.spring.data.es.entities.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {

	Book save(Book book);

	Optional<Book> findOne(String id);

	Iterable<Book> findAll();

	Page<Book> findByBookAuthor(String author, Pageable pageable);

	Page<Book> findByBookAuthorUsingCustomQuery(String author, Pageable pageable);

	Page<Book> findByFilteredYearQuery(String year, Pageable pageable);

	Page<Book> findByAuthorsNameAndFilteredYearQuery(String author, String year, Pageable pageable);

	long count();

	void delete(Book book);
}
