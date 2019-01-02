package org.apn.spring.data.es;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apn.spring.data.es.config.RestConfig;
import org.apn.spring.data.es.entities.Book;
import org.apn.spring.data.es.service.BookService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RestConfig.class)
public class ElasticsearchRestTemplateIT {

	@Autowired
	protected ElasticsearchOperations elasticsearchOperations;

	@Autowired
	private BookService bookService;

	@Autowired
	private RestHighLevelClient client;

	@Before
	public void before() {
		elasticsearchOperations.deleteIndex(Book.class);
		elasticsearchOperations.createIndex(Book.class);
		elasticsearchOperations.putMapping(Book.class);
		elasticsearchOperations.refresh(Book.class);

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
		final List<Book> books = elasticsearchOperations.queryForList(searchQuery, Book.class);
		assertEquals(1, books.size());
		assertEquals("You Know, for Search…", books.get(0).getBookTitle());
	}

	@Test
	public void testAggregation() throws IOException {
		final TermsAggregationBuilder aggregation = AggregationBuilders.terms("published_books_per_year")
				.field("yearOfPublication");
		final SearchRequest searchRequest = new SearchRequest("books");
		searchRequest.types("_doc");
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.aggregation(aggregation);
		searchRequest.source(searchSourceBuilder);

		final SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

		final Map<String, Aggregation> results = searchResponse.getAggregations().asMap();
		final ParsedLongTerms topYears = (ParsedLongTerms) results.get("published_books_per_year");

		final List<String> list = topYears.getBuckets().stream().map(MultiBucketsAggregation.Bucket::getKeyAsString)
				.sorted().collect(toList());
		assertEquals(asList("2000", "2002"), list);
	}
}
