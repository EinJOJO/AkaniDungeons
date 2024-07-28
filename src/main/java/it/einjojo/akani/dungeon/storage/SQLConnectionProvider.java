package it.einjojo.akani.dungeon.storage;

import java.sql.Connection;
import java.sql.SQLException;

public interface SQLConnectionProvider {

    Connection getConnection() throws SQLException;

}
