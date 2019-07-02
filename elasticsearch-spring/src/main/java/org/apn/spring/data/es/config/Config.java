package org.apn.spring.data.es.config;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 
 * @author amit.nema
 *
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "org.apn.spring.data.es.repository")
@ComponentScan(basePackages = { "org.apn.spring.data.es.service" })
public class Config {

	@Value("${elasticsearch.cluster.name:elasticsearch}")
	private String clusterName;

	@Value("${elasticsearch.hostName:localhost}")
	private String hostName;

	@Value("${elasticsearch.port:9300}")
	private Integer port;

	@Bean
	public Client client() throws UnknownHostException {
		TransportClient client = null;
		final Settings settings = Settings.builder()
				// --after sniffing, no further requests will go to that master node
				// -- For secure cluster see
				// https://www.elastic.co/guide/en/x-pack/current/java-clients.html
				.put("client.transport.sniff", true)//
				.put("cluster.name", clusterName).build();
		client = new PreBuiltTransportClient(settings);
		client.addTransportAddress(new TransportAddress(InetAddress.getByName(hostName), port));
		return client;
	}

	@Bean
	public ElasticsearchOperations elasticsearchTemplate() throws UnknownHostException {
		return new ElasticsearchTemplate(client());
	}
}
