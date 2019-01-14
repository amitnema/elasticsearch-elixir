package org.apn.elasticsearch.service;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apn.elasticsearch.entities.User;
import org.apn.elasticsearch.service.UserService;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * 
 * @author amit.nema
 *
 */
public class UserServiceIT {

	private static final Log LOGGER = LogFactory.getLog(UserServiceIT.class);
	private static final String INDEX = "users";
	private static RestHighLevelClient client;

	@BeforeClass
	public void beforeClass() {
		final HttpHost[] hosts = { new HttpHost("localhost", 9200) };
		client = new RestHighLevelClient(RestClient.builder(hosts));
	}

	@AfterClass
	public void afterClass() throws IOException {
		if (Objects.nonNull(client)) {
			client.close();
		}
	}

	@Test
	public void findAll() throws IOException {
		LOGGER.info("UserServiceIT.findAll()");
		List<User> list = new UserService(client).findAll(INDEX);
		assertNotNull(list);
		assertEquals(list.size(), 10);
	}
}
