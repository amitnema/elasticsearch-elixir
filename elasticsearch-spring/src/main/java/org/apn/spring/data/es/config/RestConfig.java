package org.apn.spring.data.es.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * 
 * @author amit.nema
 *
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "org.apn.spring.data.es.repository")
@ComponentScan(basePackages = { "org.apn.spring.data.es.service" })
public class RestConfig {

	@Value("${elasticsearch.hostName:localhost}")
	private String hostName;

	@Value("${elasticsearch.port:9200}")
	private Integer port;

	@Bean
	public RestHighLevelClient client() {
		return new RestHighLevelClient(RestClient.builder(new HttpHost(hostName, port)));
	}

	@Bean(name = "elasticsearchTemplate")
	public ElasticsearchOperations elasticsearchRestTemplate() {
		return new ElasticsearchRestTemplate(client());
	}
}
