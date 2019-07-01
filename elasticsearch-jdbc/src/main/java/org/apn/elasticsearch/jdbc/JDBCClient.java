package org.apn.elasticsearch.jdbc;

import org.elasticsearch.xpack.sql.jdbc.EsDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * To get connection from Elasticsearch JDBC.
 *
 * @author Amit Nema
 */
public class JDBCClient {

    private final String host;
    private final int port;
    private final String username;
    private final String password;

    public JDBCClient(final String host, final int port, final String username, final String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public Connection getConnection() throws SQLException {
        String address = "jdbc:es://" + String.join(":", host, String.valueOf(port));
        Properties connectionProperties = new Properties();
        connectionProperties.setProperty("user", username);
        connectionProperties.setProperty("password", password);
        return getConnection(address, connectionProperties);
    }

    public Connection getConnection(final String url, final Properties props) throws SQLException {
        EsDataSource dataSource = new EsDataSource();
        dataSource.setUrl(url);
        dataSource.setProperties(props);
        return dataSource.getConnection();
    }
}
