package org.apn.elasticsearch.rest;

import java.io.IOException;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author amit.nema
 *
 */
public class RestHighLevelClientAppIT {

	private static final Log LOGGER = LogFactory.getLog(RestHighLevelClientAppIT.class);

	private RestHighLevelClientApp app;

	@BeforeClass
	public void beforeClass() {
		final HttpHost[] hosts = { new HttpHost("localhost", 9200) };
		app = new RestHighLevelClientApp(hosts);
	}

	@AfterClass
	public void afterClass() throws IOException {
		if (Objects.nonNull(app)) {
			app.close();
		}
	}

	@DataProvider
	public Object[][] dpIndices() {
		return new Object[][] { new Object[] { "product" } };
	}

	@Test(dataProvider = "dpIndices")
	public void deleteIndex(final String index) throws IOException {
		boolean delete = app.deleteIndex(index);
		LOGGER.info("deleteIndex:" + delete);
	}

	@Test(dataProvider = "dpIndices", dependsOnMethods = "deleteIndex")
	public void createIndex(String index) throws IOException {
		final String settings = "{ \"index\": { \"number_of_shards\": \"1\", \"number_of_replicas\": \"1\" }}";
		LOGGER.info("createIndex:" + app.createIndex(index, settings));
	}

	@Test(dataProvider = "dpIndices", dependsOnMethods = "bulkIndex")
	public void refreshIndex(String index) throws IOException {
		LOGGER.info("refreshIndex:" + app.refreshIndex(index));
	}

	@DataProvider
	public Object[][] dpIndexService() {
		return new Object[][] { new Object[] { "product", new ProductService(app.getClient()) } };
	}

	@Test(dataProvider = "dpIndexService", dependsOnMethods = "createIndex")
	public void bulkIndex(String index, final ProductService productService) throws IOException {
		productService.bulkIndex(index);
	}

	@Test(dataProvider = "dpIndexService", dependsOnMethods = { "findById" })
	public void findMostExpensiveProducts(final String index, final ProductService productService) throws IOException {
		productService.findMostExpensiveProducts(index);
	}

	@DataProvider
	public Object[][] dpFindById() {
		return new Object[][] { new Object[] { "product", "3" } };
	}

	@Test(dataProvider = "dpFindById", dependsOnMethods = "refreshIndex")
	public void findById(String index, String id) throws IOException {
		app.findById(index, id);
	}

}
