package org.apn.elasticsearch.rest;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.util.Asserts;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

/**
 * The Class deals with Elasticsearch common usecases via
 * {@link RestHighLevelClient}</code>.
 * 
 * @author amit.nema
 *
 */
public class RestHighLevelClientApp implements Closeable {

	private static final Log LOGGER = LogFactory.getLog(RestHighLevelClientApp.class);
	private final RestHighLevelClient client;
	private final String index;
	private final String type;

	public RestHighLevelClientApp(final HttpHost[] hosts, final String index, final String type) {
		this.client = new RestHighLevelClient(RestClient.builder(hosts));
		this.index = index;
		this.type = type;
	}

	@Override
	public void close() throws IOException {
		if (Objects.nonNull(client))
			client.close();
	}

	public static void main(final String[] args) throws IOException {
		LOGGER.info("RestHighLevelClientApp.main()");
		final HttpHost[] httpHosts = { new HttpHost("localhost", 9200) };

		try (final RestHighLevelClientApp app = new RestHighLevelClientApp(httpHosts, "product", "_doc")) {
			final String settings = "{ \"index\": { \"number_of_shards\": \"1\", \"number_of_replicas\": \"1\" }}";
			LOGGER.info("deleteIndex:" + app.deleteIndex());
			LOGGER.info("createIndex:" + app.createIndex(settings));
			app.bulkIndex();
			LOGGER.info("refreshIndex:" + app.refreshIndex());
			app.findById("3");
			app.findMostExpensiveProducts();
		}
	}

	public boolean refreshIndex() {
		Asserts.notNull(index, "No index defined for refresh()");
		try {
			final RefreshResponse response = client.indices().refresh(new RefreshRequest(index),
					RequestOptions.DEFAULT);
			return response.getStatus().getStatus() == HttpStatus.SC_OK;
		} catch (final IOException e) {
			throw new ElasticsearchException("failed to refresh index: " + index, e);
		}
	}

	public void findById(final String id) throws IOException {
		final SearchRequest searchRequest = new SearchRequest(index);
		searchRequest.types(type);
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchQuery("_id", id));
		searchRequest.source(searchSourceBuilder);
		LOGGER.info("*****	source	*****\n" + searchRequest.source() + "\n*****	*****	*****");
		final SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
		final SearchHit[] searchHits = response.getHits().getHits();
		for (final SearchHit searchHit : searchHits) {
			LOGGER.info(searchHit.getSourceAsMap());
		}
	}

	public void findMostExpensiveProducts() throws IOException {
		final SearchRequest searchRequest = new SearchRequest(index);
		searchRequest.types(type);
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

	private void bulkIndex() throws IOException {
		final List<Map<String, Object>> source = getSourceToIngest();
		final BulkRequest request = new BulkRequest();
		source.forEach(
				doc -> request.add(new IndexRequest(index, type, Objects.toString(doc.get("ProductID"))).source(doc)));

		// multiple requests can be added
		request.add(new DeleteRequest(index, type, "4"));

		request.timeout("2m");
		final BulkResponse bulkResponse = client.bulk(request, RequestOptions.DEFAULT);
		checkBulkResponse(bulkResponse);
	}

	private void checkBulkResponse(final BulkResponse bulkResponse) {
		for (final BulkItemResponse bulkItemResponse : bulkResponse) {
			final DocWriteResponse itemResponse = bulkItemResponse.getResponse();

			if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.INDEX
					|| bulkItemResponse.getOpType() == DocWriteRequest.OpType.CREATE) {
				final IndexResponse indexResponse = (IndexResponse) itemResponse;
				LOGGER.info(indexResponse);
			} else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.UPDATE) {
				final UpdateResponse updateResponse = (UpdateResponse) itemResponse;
				LOGGER.info(updateResponse);
			} else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.DELETE) {
				final DeleteResponse deleteResponse = (DeleteResponse) itemResponse;
				LOGGER.info(deleteResponse);
			}
		}
	}

	public boolean deleteIndex() throws IOException {
		Asserts.notNull(client, "Client must not be empty. Please call init().");
		final DeleteIndexRequest request = new DeleteIndexRequest(index);
		return indexExists() && client.indices().delete(request, RequestOptions.DEFAULT).isAcknowledged();
	}

	public boolean indexExists() {
		final GetIndexRequest request = new GetIndexRequest();
		request.indices(index);
		try {
			return client.indices().exists(request, RequestOptions.DEFAULT);
		} catch (final IOException e) {
			throw new ElasticsearchException("Error while for indexExists request: " + request.toString(), e);
		}
	}

	public boolean createIndex(final String settings) throws IOException {
		Asserts.notNull(client, "Client must not be empty. Please call init().");
		final CreateIndexRequest request = new CreateIndexRequest(index);
		if (Objects.nonNull(settings)) {
			request.settings(String.valueOf(settings), XContentType.JSON);
		}
		return !indexExists() && client.indices().create(request, RequestOptions.DEFAULT).isAcknowledged();
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