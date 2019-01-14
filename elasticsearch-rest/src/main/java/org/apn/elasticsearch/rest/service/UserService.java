/**
 * 
 */
package org.apn.elasticsearch.rest.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apn.User;
import org.apn.elasticsearch.rest.utils.Constants;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author amit.nema
 *
 */
public class UserService {

	private static final Log LOGGER = LogFactory.getLog(UserService.class);

	private final RestHighLevelClient client;

	public UserService(final RestHighLevelClient client) {
		this.client = client;
	}

	public List<User> findAll(final String index) throws IOException {
		LOGGER.info("UserService.findAll()");
		final List<User> users = new ArrayList<>();
		final SearchRequest searchRequest = new SearchRequest(index);
		searchRequest.types(Constants.DEFAULT_TYPE);
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchAllQuery());
		searchRequest.source(searchSourceBuilder);
		final SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
		final SearchHit[] searchHits = response.getHits().getHits();
		for (final SearchHit searchHit : searchHits) {
			final ObjectMapper mapper = new ObjectMapper();
			users.add(mapper.convertValue(searchHit.getSourceAsMap(), User.class));
		}
		return users;
	}
}
