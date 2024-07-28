package it.einjojo.akani.dungeon.storage.mine;

import it.einjojo.akani.dungeon.storage.TestSQLConnectionProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SQLMineStorageTest {

    private TestSQLConnectionProvider connectionProvider;
    private SQLMineStorage storage;

    @BeforeEach
    void setUp() {
        connectionProvider = new TestSQLConnectionProvider("dungeons", "root", "password");
        storage = new SQLMineStorage(connectionProvider);
    }

    @Test
    void testInit() {
        storage.init();
    }



}