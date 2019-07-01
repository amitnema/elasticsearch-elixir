package org.apn.elasticsearch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apn.elasticsearch.service.ProductService;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Objects;

/**
 * @author amit.nema
 */
public class RestHighLevelClientAppIT {

    private static final Log LOGGER = LogFactory.getLog(RestHighLevelClientAppIT.class);

    private RestHighLevelClientApp app;

    @BeforeClass
    public void beforeClass() {
        final HttpHost[] hosts = {new HttpHost("localhost", 9200)};
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
        return new Object[][]{new Object[]{"product"}};
    }

    @Test(dataProvider = "dpIndices")
    public void testDeleteIndex(final String index) throws IOException {
        final boolean delete = app.deleteIndex(index);
        LOGGER.info("testDeleteIndex:" + delete);
    }

    @Test(dataProvider = "dpIndices", dependsOnMethods = "testDeleteIndex")
    public void testCreateIndex(final String index) throws IOException {
        final String settings = "{ \"index\": { \"number_of_shards\": \"1\", \"number_of_replicas\": \"1\" }}";
        LOGGER.info("testCreateIndex:" + app.createIndex(index, settings));
    }

    @Test(dataProvider = "dpIndices", dependsOnMethods = "bulkIndex")
    public void testRefreshIndex(final String index) {
        LOGGER.info("testRefreshIndex:" + app.refreshIndex(index));
    }

    @DataProvider
    public Object[][] dpIndexService() {
        return new Object[][]{new Object[]{"product", new ProductService(app.getClient())}};
    }

    @Test(dataProvider = "dpIndexService", dependsOnMethods = "testCreateIndex")
    public void bulkIndex(final String index, final ProductService productService) throws IOException {
        productService.bulkIndex(app, index);
    }

    @Test(dataProvider = "dpIndexService", dependsOnMethods = {"testFindById"})
    public void testFindMostExpensiveProducts(final String index, final ProductService productService) throws IOException {
        productService.findMostExpensiveProducts(index);
    }

    @DataProvider
    public Object[][] dpFindById() {
        return new Object[][]{new Object[]{"product", "3"}};
    }

    @Test(dataProvider = "dpFindById", dependsOnMethods = "testRefreshIndex")
    public void testFindById(final String index, final String id) throws IOException {
        app.findById(index, id);
    }

}
