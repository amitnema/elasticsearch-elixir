package org.apn.spring.data.es;

import org.apn.spring.data.es.config.Config;
import org.apn.spring.data.es.entities.Book;
import org.apn.spring.data.es.service.BookService;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Config.class)
public class ElasticsearchTemplateIT {

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	@Autowired
	private BookService bookService;

	@Autowired
	private Client client;

	@Before
	public void before() {
		elasticsearchTemplate.deleteIndex(Book.class);
		elasticsearchTemplate.createIndex(Book.class);
		elasticsearchTemplate.putMapping(Book.class);
		elasticsearchTemplate.refresh(Book.class);

		Book book = new Book("Elasticsearch Spring");
		book.setIsbn(UUID.randomUUID().toString());
		book.setBookAuthor("Amit Nema");
		book.setYearOfPublication(2002L);
		bookService.save(book);

		book = new Book("You Know, for Search…");
		book.setIsbn(UUID.randomUUID().toString());
		book.setBookAuthor("Prakash");
		book.setYearOfPublication(2000L);
		bookService.save(book);

		book = new Book("What If?: The World's Foremost Military Historians Imagine What Might Have Been");
		book.setIsbn(UUID.randomUUID().toString());
		book.setBookAuthor("Robert Cowley");
		book.setYearOfPublication(2000L);
		bookService.save(book);

		book = new Book("Where You'll Find Me: And Other Stories");
		book.setIsbn(UUID.randomUUID().toString());
		book.setBookAuthor("Ann Beattie");
		book.setYearOfPublication(2002L);
		bookService.save(book);
	}

	@Test
	public void testSearch() {
		final SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchQuery("bookTitle", "You Know"))
				.build();
		final List<Book> books = elasticsearchTemplate.queryForList(searchQuery, Book.class);
		assertEquals(1, books.size());
		assertEquals("You Know, for Search…", books.get(0).getBookTitle());
	}

	@Test
	public void testAggregation() {
		final TermsAggregationBuilder aggregation = AggregationBuilders.terms("published_books_per_year")
				.field("yearOfPublication");
		SearchRequestBuilder requestBuilder = client.prepareSearch("books").setTypes("_doc")
				.addAggregation(aggregation);
		final SearchResponse response = requestBuilder.execute().actionGet();

		final Map<String, Aggregation> results = response.getAggregations().asMap();
		final LongTerms topYears = (LongTerms) results.get("published_books_per_year");

		final List<String> list = topYears.getBuckets().stream().map(MultiBucketsAggregation.Bucket::getKeyAsString)
				.sorted().collect(toList());
		assertEquals(asList("2000", "2002"), list);
	}
}
