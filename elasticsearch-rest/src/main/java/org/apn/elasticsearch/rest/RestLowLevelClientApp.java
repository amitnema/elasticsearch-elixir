package org.apn.elasticsearch.rest;

import java.io.Closeable;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

/**
 * The Class deals with Elasticsearch common usecases via
 * {@link RestClient}</code>.
 * 
 * @author amit.nema
 *
 */
public class RestLowLevelClientApp implements Closeable {

	private static final Log LOGGER = LogFactory.getLog(RestLowLevelClientApp.class);
	private final RestClient client;
	private final String index;
	private final String type;

	public RestLowLevelClientApp(final HttpHost[] hosts, final String index, final String type) {
		this.index = index;
		this.type = type;
		client = RestClient.builder(hosts).build();
	}

	public void close() throws IOException {
		if (Objects.nonNull(client))
			client.close();
	}

	public void findById(final String id) throws IOException {
		final Request req = new Request(HttpGet.METHOD_NAME, String.join("/", index, type, "_search"));
		req.addParameter("pretty", "false");
		req.setJsonEntity("{\"query\":{\"match\":{\"" + "_id" + "\":\"" + id + "\"}}}");
		final Response resp = client.performRequest(req);
		LOGGER.info("*****	source	*****\n" + resp.getRequestLine() + "\n*****	*****	*****");
		final String responseBody = EntityUtils.toString(resp.getEntity());
		LOGGER.info(responseBody);
	}

	public boolean indexExists() throws IOException {
		final Request req = new Request(HttpHead.METHOD_NAME, MessageFormat.format("/{0}", index));
		final Response response = client.performRequest(req);
		return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
	}

	public boolean refreshIndex() throws IOException {
		final Request req = new Request(HttpPost.METHOD_NAME, String.join("/", index, type, "_refresh"));
		req.setJsonEntity("{}");
		final Response response = client.performRequest(req);
		return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
	}

	public static void main(final String[] args) throws IOException {
		LOGGER.info("RestLowLevelClientApp.main()");
		try (final RestLowLevelClientApp app = new RestLowLevelClientApp(
				new HttpHost[] { new HttpHost("localhost", 9200) }, "product", "_doc")) {
			LOGGER.info("indexExists:" + app.indexExists());
			app.refreshIndex();
			app.findById("3");
		}
	}
}