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


------------------------------------------------------------
<sub>1: This module is not related to parent pom</sub>