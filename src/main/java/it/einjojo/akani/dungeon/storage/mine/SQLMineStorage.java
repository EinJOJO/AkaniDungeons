package it.einjojo.akani.dungeon.storage.mine;

import it.einjojo.akani.dungeon.mine.MineOreType;
import it.einjojo.akani.dungeon.mine.PlacedOre;
import it.einjojo.akani.dungeon.mine.factory.PlacedOreFactory;
import it.einjojo.akani.dungeon.storage.SQLConnectionProvider;
import it.einjojo.akani.dungeon.storage.StorageException;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class SQLMineStorage {

    private static final Logger log = LoggerFactory.getLogger(SQLMineStorage.class);
    private final SQLConnectionProvider connectionProvider;


    public SQLMineStorage(SQLConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public void init() {
        try (var connection = connectionProvider.getConnection()) {
            var statement = connection.createStatement();
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS dungeons_mine_type
                    (
                        id               INT PRIMARY KEY AUTO_INCREMENT,
                        name             VARCHAR(255) NOT NULL UNIQUE,
                        icon_nbt         TEXT         NOT NULL,
                        max_health       INT          NOT NULL,
                        tool_type        TINYINT      NOT NULL,
                        tool_hardness    TINYINT      NOT NULL,
                        respawn_duration INT          NOT NULL
                    );""");
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS dungeons_mine_type_rewards
                    (
                        id      INT PRIMARY KEY AUTO_INCREMENT,
                        type_id      INT   NOT NULL,
                        item_nbt     TEXT  NOT NULL,
                        chance       FLOAT NOT NULL,
                        min_quantity INT   NOT NULL,
                        max_quantity INT   NOT NULL,
                        FOREIGN KEY (type_id) REFERENCES dungeons_mine_type (id)
                    );""");
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS dungeons_mine_placed
                    (
                        id    INT PRIMARY KEY AUTO_INCREMENT,
                        type  VARCHAR(255) NOT NULL,
                        world VARCHAR(255) NOT NULL,
                        x     DOUBLE         NOT NULL,
                        y     DOUBLE         NOT NULL,
                        z     DOUBLE         NOT NULL,
                        pitch FLOAT          NOT NULL,
                        yaw   FLOAT          NOT NULL
                    );""");
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS dungeons_mine_placed_destroyed
                    (
                        destroyer_player_uuid VARCHAR(32) NOT NULL,
                        placed_ore_id         INT         NOT NULL,
                        destroyed_at          TIMESTAMP   NOT NULL,
                        expiry                TIMESTAMP   NOT NULL
                    );""");
        } catch (SQLException ex) {
            throw new StorageException("initializing placed mine ore storage", ex);
        }
    }


    public void createPlacedOre(PlacedOre placedOre) {
        if (placedOre.databaseId() != null) {
            throw new IllegalArgumentException("placed ore already exists in database");
        }
        String sql = """
                INSERT INTO dungeons_mine_placed (type, x, y, z, pitch, yaw, world)
                VALUES (?, ?, ?, ?, ?, ?, ?);""";
        try (var connection = connectionProvider.getConnection(); var ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, placedOre.type().name());
            ps.setDouble(2, (float) placedOre.location().x());
            ps.setDouble(3, placedOre.location().y());
            ps.setDouble(4, placedOre.location().z());
            ps.setFloat(5, placedOre.location().getPitch());
            ps.setFloat(6, placedOre.location().getYaw());
            ps.setString(7, placedOre.location().getWorld().getName());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    placedOre.setDatabaseId(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
            throw new StorageException("creating placed ore: ", ex);
        }
    }

    public void deletePlacedOre(PlacedOre ore) {
        Integer id = ore.databaseId();
        if (id == null) {
            throw new IllegalArgumentException("placed ore has no database_id set");
        }
        deletePlacedOre(id);
        ore.setDatabaseId(null);
    }

    public void deletePlacedOre(int id) {
        String sql = """
                DELETE FROM dungeons_mine_placed
                WHERE id = ?;""";
        try (var connection = connectionProvider.getConnection(); var ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new StorageException("deleting placed ore: " + id, ex);
        }
    }

    /**
     * Updates an existing placed ore in the database
     *
     * @param placedOre placedOre with database_id set
     * @throws IllegalArgumentException if placedOre has no database_id set
     */
    public void updatePlacedOre(PlacedOre placedOre) {
        Integer id = placedOre.databaseId();
        if (id == null) {
            throw new IllegalArgumentException("placed ore has no database_id set");
        }
        String sql = """
                UPDATE dungeons_mine_placed
                SET type = ?, x = ?, y = ?, z = ?, pitch = ?, yaw = ?, world = ?
                WHERE id = ?;""";
        try (var connection = connectionProvider.getConnection(); var ps = connection.prepareStatement(sql)) {
            ps.setString(1, placedOre.type().name());
            ps.setDouble(2, placedOre.location().x());
            ps.setDouble(3, placedOre.location().y());
            ps.setDouble(4, placedOre.location().z());
            ps.setFloat(5, placedOre.location().getPitch());
            ps.setFloat(6, placedOre.location().getYaw());
            ps.setString(7, placedOre.location().getWorld().getName());
            ps.setInt(7, id);
            ps.executeUpdate();
        } catch (Exception ex) {
            throw new StorageException("updating placed ore: " + id, ex);
        }
    }

    /**
     * @param id                          the id of the placed ore
     * @param factory                     the factory to create the placed ore
     * @param oreTypeByNameLookupFunction a function to lookup the ore type by name
     * @return a placed ore
     * @throws StorageException on failure
     */
    public @Nullable PlacedOre loadPlacedOre(int id, PlacedOreFactory factory, Function<String, MineOreType> oreTypeByNameLookupFunction) {
        String sql = """
                SELECT id, type, x, y, z, pitch, yaw, world
                FROM dungeons_mine_placed
                WHERE id = ?;""";
        try (var connection = connectionProvider.getConnection(); var ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return placedOreFromResultSet(rs, factory, oreTypeByNameLookupFunction);
                } else {
                    return null;
                }
            }
        } catch (Exception ex) {
            throw new StorageException("loading placed ore: " + id, ex);
        }
    }

    /**
     * @param placedOreFactory            factory
     * @param oreTypeByNameLookupFunction lookup function
     * @return list of all placed ores
     */
    public @NotNull List<PlacedOre> loadAllPlacedOres(PlacedOreFactory placedOreFactory, Function<String, MineOreType> oreTypeByNameLookupFunction) {
        String sql = """
                SELECT id, type, x, y, z, pitch, yaw, world
                FROM dungeons_mine_placed;""";
        List<PlacedOre> list = new LinkedList<>();
        try (var connection = connectionProvider.getConnection(); var ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    try {
                        list.add(placedOreFromResultSet(rs, placedOreFactory, oreTypeByNameLookupFunction));
                    } catch (IllegalStateException worldNotLoaded) {
                        log.warn("skipping placed ore because world not loaded: {}", worldNotLoaded.getMessage());
                    } catch (NoSuchElementException ex) {
                        int ore = rs.getInt("id");
                        log.info("deleting placed ore because type is invalid: {}", ore);
                        deletePlacedOre(ore);
                    }
                }
            }
        } catch (Exception ex) {
            throw new StorageException("loading all placed ores", ex);
        }
        return list;
    }

    /**
     * @param rs                          resultSet
     * @param factory                     factory
     * @param oreTypeByNameLookupFunction oreTypeByNameLookupFunction
     * @return PlacedOre from resultSet
     * @throws SQLException on failure
     */
    public @NotNull PlacedOre placedOreFromResultSet(ResultSet rs, PlacedOreFactory factory, Function<String, MineOreType> oreTypeByNameLookupFunction) throws SQLException {
        String type = rs.getString("type");
        String worldName = rs.getString("world");
        int id = rs.getInt("id");
        double x = rs.getDouble("x");
        double y = rs.getDouble("y");
        double z = rs.getDouble("z");
        float pitch = rs.getFloat("pitch");
        float yaw = rs.getFloat("yaw");
        World world = Bukkit.getWorld(worldName);
        if (world == null) throw new IllegalStateException("world not found: " + worldName);
        var placedOre = factory.createPlacedOre(new Location(world, x, y, z, yaw, pitch), oreTypeByNameLookupFunction.apply(type));
        placedOre.setDatabaseId(id);
        return placedOre;

    }
}
