![Java CI with Maven](https://github.com/amitnema/elasticsearch-elixir/workflows/Java%20CI%20with%20Maven/badge.svg) | [![Build Status](https://travis-ci.org/amitnema/elasticsearch-elixir.svg?branch=master)](https://travis-ci.org/amitnema/elasticsearch-elixir)  |  [![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=org.apn.elasticsearch%3Aelasticsearch-elixir&metric=alert_status)](https://sonarcloud.io/dashboard?id=org.apn.elasticsearch%3Aelasticsearch-elixir)  |  [![Sputnik](https://sputnik.ci/conf/badge)](https://sputnik.ci/app#/builds/amitnema/elasticsearch-elixir)  |  [![CircleCI](https://circleci.com/gh/amitnema/elasticsearch-elixir.svg?style=svg)](https://circleci.com/gh/amitnema/elasticsearch-elixir)


# Elasticsearch Elixir
This contains the general purpose solutions for elasticsearch engine.

##### Build
	mvn clean verify

#### Elasticsearch Migration
This module contains the way to convert the solr schema file xml file to elasticsearch settings-mappings json file.

##### USE
	java -jar elasticsearch-migration-${version}-jar-with-dependencies.jar -inputFile <path/to/conf/schema.xml> -shards <number_of_shards (default=5)>  -replicas <number_of_replicas (default=1)>
	
> *   inputFile : Madatory
*	shards : default=5
*	replicas : default=1

O/p: elasticsearch settings-mappings file can be find at 'path/to/conf/schema.xml.json'

#### Elasticsearch Spark
##### USE
	See org.apn.elasticsearch.spark.client.ESIndexDataTest.scala


#### Elasticsearch Spring
##### USE
	see org.apn.elasticsearch.spark.client.ElasticSearchSpringIntegrationTest.java

#### Elasticsearch Plugin<sup>1</sup>
##### Install the Plugin
	sudo bin/elasticsearch-plugin install file:///C:/path/to/elasticsearch-custom-plugin-${version}.zip	

##### Remove the Plugin
	sudo bin/elasticsearch-plugin remove elasticsearch-custom-plugin


##### Elasticsearch Rest
This is an example to use Elasticsearch high level rest api.


------------------------------------------------------------
<sub>1: This module is not related to parent pom</sub>
