package org.apn.spring.data.es.service;

import org.apn.spring.data.es.entities.Book;
import org.apn.spring.data.es.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

	private final BookRepository bookRepository;

	@Autowired
	public BookServiceImpl(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

	@Override
	public Book save(Book book) {
		return bookRepository.save(book);
	}

	@Override
	public Optional<Book> findOne(String id) {
		return bookRepository.findById(id);
	}

	@Override
	public Iterable<Book> findAll() {
		return bookRepository.findAll();
	}

	@Override
	public Page<Book> findByBookAuthor(String author, Pageable pageable) {
		return bookRepository.findByBookAuthor(author, pageable);
	}

	@Override
	public Page<Book> findByBookAuthorUsingCustomQuery(String author, Pageable pageable) {
		return bookRepository.findByBookAuthorUsingCustomQuery(author, pageable);
	}

	@Override
	public Page<Book> findByFilteredYearQuery(String year, Pageable pageable) {
		return bookRepository.findByFilteredYearQuery(year, pageable);
	}

	@Override
	public Page<Book> findByAuthorsNameAndFilteredYearQuery(String author, String year, Pageable pageable) {
		return bookRepository.findByBookAuthorAndFilteredYearQuery(author, year, pageable);
	}

	@Override
	public long count() {
		return bookRepository.count();
	}

	@Override
	public void delete(Book book) {
		bookRepository.delete(book);
	}

}
