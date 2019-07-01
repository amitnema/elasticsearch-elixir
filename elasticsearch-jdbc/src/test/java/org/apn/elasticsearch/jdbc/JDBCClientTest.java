package org.apn.elasticsearch.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;

import static org.junit.Assert.assertNotNull;

/**
 * Test {@link JDBCClient} class.
 * This test required a elasticsearch cluster to be setup locally.
 *
 * @Amit Nema
 */
public class JDBCClientTest {

    private JDBCClient jdbcClient;

    @Before
    public void setUp() {
        jdbcClient = new JDBCClient("localhost", 9200, "elastic", "elastic");
    }

    @After
    public void tearDown() {
        jdbcClient =null;
    }

    @Test
    public void executeQuery() throws SQLException {
        StringBuilder outTable = new StringBuilder();

        try (Connection conn = jdbcClient.getConnection()) {
            try (Statement statement = conn.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery("select * from product")) {
                    assertNotNull(resultSet);
                    //Get column Names
                    ResultSetMetaData metadata = resultSet.getMetaData();
                    int columnCount = metadata.getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        outTable.append(metadata.getColumnName(i));
                        if (i != columnCount)
                            outTable.append(", ");
                    }
                    outTable.append("\n");
                    //Get Row Data
                    while (resultSet.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            outTable.append(resultSet.getString(i));
                            if (i != columnCount)
                                outTable.append(", ");
                        }
                        outTable.append("\n");
                    }
                    assertNotNull(outTable);
                }
            }
        }
    }
}