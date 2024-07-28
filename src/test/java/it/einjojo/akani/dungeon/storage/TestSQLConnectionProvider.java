package it.einjojo.akani.dungeon.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;

public class TestSQLConnectionProvider implements SQLConnectionProvider {

    private final Connection connection;

    public TestSQLConnectionProvider(String dbName, String user, String password) {
        try {
            connection = DriverManager.getConnection(MessageFormat.format("jdbc:mariadb://localhost:3306/{0}?user={1}&password={2}", dbName, user, password));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public Connection getConnection() {
        return connection;
    }
}
