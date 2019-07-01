# Elasticsearch JDBC
This contains the general purpose solutions for elasticsearch SQL through elasticsearch-JDBC.

## Build
	mvn clean verify

## Prerequisite 

> &#9432;
*JDBC client requires a Platinum subscription. Please ensure that you have a Trial or Platinum license installed on your cluster before proceeding.
(Reference: [jdbc-client](https://www.elastic.co/downloads/jdbc-client))*

### Activate the trial license
* Activate the security by editing following lines inside elasticsearch.yml file.
    ```yaml
    xpack.graph.enabled: true
    xpack.ml.enabled: true
    xpack.monitoring.enabled: true
    xpack.security.enabled: true
    xpack.watcher.enabled: true
    xpack.security.transport.ssl.enabled: true
    ``` 

* Set the password [setup-passwords](https://www.elastic.co/guide/en/elasticsearch/reference/current/setup-passwords.html)
    ```bash
    bin/elasticsearch-setup-passwords interactive
    ```

* Setup the elasticsearch password inside file kibana.yml (kibana config).
    ```yaml
    elasticsearch.username: "username"
    elasticsearch.password: "password"
    ```
* Start a 30-day trial license.

    The following example starts a 30-day trial license. The acknowledge parameter is required as you are initiating a license that will expire.
    ```bash
    POST /_license/start_trial?acknowledge=true
    ```
    
