package org.apn.elasticsearch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Objects;

public class RestLowLevelClientAppIT {

    private static final Log LOGGER = LogFactory.getLog(RestLowLevelClientAppIT.class);

    private RestLowLevelClientApp app;

    @BeforeMethod
    public void setUp() {
        final HttpHost[] hosts = {new HttpHost("localhost", 9200)};
        app = new RestLowLevelClientApp(hosts);
    }

    @AfterMethod
    public void tearDown() throws IOException {
        if (Objects.nonNull(app)) {
            app.close();
        }
    }

    @DataProvider
    public Object[][] dpFindById() {
        return new Object[][]{new Object[]{"product", "3"}};
    }

    @Test(dataProvider = "dpFindById", dependsOnMethods = "testRefreshIndex")
    public void testFindById(final String index, final String id) throws IOException {
        app.findById(index, id);
    }

    @Test(dataProvider = "dpFindById", dependsOnMethods = "testRefreshIndex")
    public void testFindByIdSQL(final String index, final String id) throws IOException {
        app.findByIdSQL(index, id);
    }

    @DataProvider
    public Object[][] dpIndices() {
        return new Object[][]{new Object[]{"product"}};
    }

    @Test(dataProvider = "dpIndices")
    public void testIndexExists(final String index) throws IOException {
        final boolean exists = app.indexExists(index);
        LOGGER.info("testIndexExists:" + exists);
    }

    @Test(dataProvider = "dpIndices")
    public void testRefreshIndex(final String index) throws IOException {
        LOGGER.info("testRefreshIndex:" + app.refreshIndex(index));
    }
}