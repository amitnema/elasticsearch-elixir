package org.apn.spring.data.es.repository;

import org.apn.spring.data.es.entities.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends ElasticsearchRepository<Book, String> {

	Page<Book> findByBookAuthor(String author, Pageable pageable);

	@Query("{\"bool\": {\"must\": [{\"match\": {\"bookAuthor\": \"?0\"}}]}}")
	Page<Book> findByBookAuthorUsingCustomQuery(String author, Pageable pageable);

	@Query("{\"bool\": {\"must\": {\"match_all\": {}}, \"filter\": {\"term\": {\"yearOfPublication\": \"?0\" }}}}")
	Page<Book> findByFilteredYearQuery(String year, Pageable pageable);

	@Query("{\"bool\": {\"must\": {\"match\": {\"bookAuthor\": \"?0\"}}, \"filter\": {\"term\": {\"yearOfPublication\": \"?1\" }}}}")
	Page<Book> findByBookAuthorAndFilteredYearQuery(String author, String year, Pageable pageable);
}
