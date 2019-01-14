package org.apn.elasticsearch;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.util.Asserts;
import org.apn.elasticsearch.utils.Constants;
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
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

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

	public RestHighLevelClientApp(final HttpHost[] hosts) {
		this.client = new RestHighLevelClient(RestClient.builder(hosts));
	}

	public RestHighLevelClientApp(final RestHighLevelClient client) {
		this.client = client;
	}

	public RestHighLevelClient getClient() {
		return client;
	}

	@Override
	public void close() throws IOException {
		if (Objects.nonNull(client))
			client.close();
	}

	public boolean refreshIndex(final String index) {
		Asserts.notNull(index, "No index defined for refresh()");
		try {
			final RefreshResponse response = client.indices().refresh(new RefreshRequest(index),
					RequestOptions.DEFAULT);
			return response.getStatus().getStatus() == HttpStatus.SC_OK;
		} catch (final IOException e) {
			throw new ElasticsearchException("failed to refresh index: " + index, e);
		}
	}

	public void findById(final String index, final String id) throws IOException {
		final SearchRequest searchRequest = new SearchRequest(index);
		searchRequest.types(Constants.DEFAULT_TYPE);
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

	public void bulkIndex(final String index, final String idKey, final List<Map<String, Object>> source)
			throws IOException {
		final BulkRequest request = new BulkRequest();
		source.forEach(doc -> request
				.add(new IndexRequest(index, Constants.DEFAULT_TYPE, Objects.toString(doc.get(idKey))).source(doc)));

		// multiple requests can be added
		request.add(new DeleteRequest(index, Constants.DEFAULT_TYPE, "4"));

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

	public boolean deleteIndex(final String index) throws IOException {
		Asserts.notNull(client, "Client must not be empty. Please call init().");
		final DeleteIndexRequest request = new DeleteIndexRequest(index);
		return indexExists(index) && client.indices().delete(request, RequestOptions.DEFAULT).isAcknowledged();
	}

	public boolean indexExists(final String index) {
		final GetIndexRequest request = new GetIndexRequest();
		request.indices(index);
		try {
			return client.indices().exists(request, RequestOptions.DEFAULT);
		} catch (final IOException e) {
			throw new ElasticsearchException("Error while for indexExists request: " + request.toString(), e);
		}
	}

	public boolean createIndex(final String index, final String settings) throws IOException {
		Asserts.notNull(client, "Client must not be empty. Please call init().");
		final CreateIndexRequest request = new CreateIndexRequest(index);
		if (Objects.nonNull(settings)) {
			request.settings(String.valueOf(settings), XContentType.JSON);
		}
		return !indexExists(index) && client.indices().create(request, RequestOptions.DEFAULT).isAcknowledged();
	}

}