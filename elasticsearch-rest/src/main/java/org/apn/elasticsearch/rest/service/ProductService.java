/**
 * 
 */
package org.apn.elasticsearch.rest.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apn.elasticsearch.rest.RestHighLevelClientApp;
import org.apn.elasticsearch.rest.utils.Constants;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

/**
 * @author amit.nema
 *
 */
public class ProductService {

	private static final Log LOGGER = LogFactory.getLog(ProductService.class);

	private final RestHighLevelClient client;

	public ProductService(final RestHighLevelClient client) {
		this.client = client;
	}

	public void findMostExpensiveProducts(final String index) throws IOException {
		final SearchRequest searchRequest = new SearchRequest(index);
		searchRequest.types(Constants.DEFAULT_TYPE);
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchAllQuery());
		searchSourceBuilder.sort("UnitPrice", SortOrder.DESC);
		searchSourceBuilder.fetchSource(new String[] { "productName", "UnitPrice" }, null);
		searchRequest.source(searchSourceBuilder);
		LOGGER.info("*****	source	*****\n" + searchRequest.source() + "\n*****	*****	*****");
		final SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
		final SearchHit[] searchHits = response.getHits().getHits();
		for (final SearchHit searchHit : searchHits) {
			LOGGER.info(searchHit.getSourceAsMap());
		}
	}

	public void bulkIndex(final RestHighLevelClientApp app, final String index) throws IOException {
		app.bulkIndex(index, "ProductID", getSourceToIngest());
	}

	private List<Map<String, Object>> getSourceToIngest() {
		final List<Map<String, Object>> source = new ArrayList<>();

		final String[] keys = Strings.commaDelimitedListToStringArray(
				"ProductID,ProductName,SupplierID,CategoryID,QuantityPerUnit,UnitPrice,UnitsInStock,UnitsOnOrder,ReorderLevel,Discontinued");

		final List<String> lines = Arrays.asList("1,Chai,1,1,10 boxes x 20 bags,18,39,0,10,0",
				"2,Chang,1,1,24 - 12 oz bottles,19,17,40,25,0",
				"3,Aniseed Syrup,1,2,12 - 550 ml bottles,10,13,70,25,0");
		lines.forEach(document -> {
			final Map<String, Object> doc = new HashMap<>();
			final String[] fields = Strings.commaDelimitedListToStringArray(document);
			for (int i = 0; i < keys.length; i++) {
				doc.put(keys[i], getFields(fields[i]));
			}
			source.add(doc);
		});
		return source;
	}

	private Object getFields(final String string) {
		Object obj = string;
		try {
			obj = Integer.parseInt(string);
		} catch (final NumberFormatException e) {
			// DO Nothing
		}
		return obj;
	}
}
