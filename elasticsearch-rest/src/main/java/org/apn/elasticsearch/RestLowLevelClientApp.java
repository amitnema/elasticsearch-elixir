package org.apn.elasticsearch;

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

import java.io.Closeable;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Objects;

/**
 * The Class deals with Elasticsearch common usecases via
 * {@link RestClient}</code>.
 *
 * @author amit.nema
 */
public class RestLowLevelClientApp implements Closeable {

    private static final Log LOGGER = LogFactory.getLog(RestLowLevelClientApp.class);
    private final RestClient client;

    public RestLowLevelClientApp(final HttpHost... hosts) {
        client = RestClient.builder(hosts).build();
    }

    @Override
    public void close() throws IOException {
        if (Objects.nonNull(client))
            client.close();
    }

    public void findById(final String index, final String id) throws IOException {
        final Request req = new Request(HttpGet.METHOD_NAME, String.join("/", index, "_search"));
        req.addParameter("pretty", "false");
        req.setJsonEntity("{\"query\":{\"match\":{\"" + "_id" + "\":\"" + id + "\"}}}");
        final Response resp = client.performRequest(req);
        LOGGER.info("*****	source	*****\n" + resp.getRequestLine() + "\n*****	*****	*****");
        final String responseBody = EntityUtils.toString(resp.getEntity());
        LOGGER.info(responseBody);
    }

    public void findByIdSQL(final String index, final String id) throws IOException {
        final Request req = new Request(HttpGet.METHOD_NAME, "_sql");
        req.addParameter("pretty", "false");
        req.setJsonEntity("{\"query\":\"select * from " + index + " where ProductID = " + id + "\"}");
        final Response resp = client.performRequest(req);
        LOGGER.info("*****	source	*****\n" + resp.getRequestLine() + "\n*****	*****	*****");
        final String responseBody = EntityUtils.toString(resp.getEntity());
        LOGGER.info(responseBody);
    }

    public boolean indexExists(final String index) throws IOException {
        final Request req = new Request(HttpHead.METHOD_NAME, MessageFormat.format("/{0}", index));
        final Response response = client.performRequest(req);
        return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
    }

    public boolean refreshIndex(final String index) throws IOException {
        final Request req = new Request(HttpPost.METHOD_NAME, String.join("/", index, "_refresh"));
        req.setJsonEntity("{}");
        final Response response = client.performRequest(req);
        return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
    }
}