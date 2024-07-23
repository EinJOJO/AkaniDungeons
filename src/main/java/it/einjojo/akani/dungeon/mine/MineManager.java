package it.einjojo.akani.dungeon.mine;

import com.google.common.base.Preconditions;
import it.einjojo.akani.dungeon.config.MineOreTypeConfig;
import it.einjojo.akani.dungeon.mine.factory.PlacedOreFactory;
import it.einjojo.akani.dungeon.storage.SQLConnectionProvider;
import it.einjojo.akani.dungeon.storage.mine.SQLMineStorage;
import it.einjojo.akani.dungeon.util.ChunkPosition;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MineManager {

    private static final Logger logger = LoggerFactory.getLogger(MineManager.class);
    private final Map<ChunkPosition, MineChunk> oreChunkMap = new ConcurrentHashMap<>();
    private final Map<Integer, PlacedOre> oreMap = new ConcurrentHashMap<>();
    private final Map<UUID, MineProgression> progressionMap = new ConcurrentHashMap<>();
    private final MineOreTypeConfig config;
    private final SQLMineStorage storage;


    public MineManager(MineOreTypeConfig config, SQLConnectionProvider connectionProvider) {
        Preconditions.checkNotNull(config);
        this.config = config;
        storage = new SQLMineStorage(connectionProvider);
        storage.init();
    }

    public PlacedOre oreByEntityId(int entityId) {
        return oreMap.get(entityId);
    }

    public @Nullable MineChunk chunkByPosition(ChunkPosition position) {
        return oreChunkMap.get(position);
    }

    public MineProgression progressionByPlayer(UUID playerId) {
        return progressionMap.get(playerId);
    }

    public void createProgression(UUID playerId) {
        progressionMap.put(playerId, new MineProgression());
    }

    public void removeProgression(UUID playerId) {
        progressionMap.remove(playerId);
    }

    public void registerPlacedOre(PlacedOre ore) {
        oreMap.put(ore.entityId(), ore);
        MineChunk chunk = oreChunkMap.computeIfAbsent(ChunkPosition.of(ore.location()), chunkPosition -> new MineChunk(chunkPosition, new LinkedList<>()));
        chunk.ores().add(ore);
        logger.debug("Registered ore {}", ore.entityId());
    }

    public void unregisterPlacedOre(PlacedOre ore) {
        oreMap.remove(ore.entityId());
        oreChunkMap.get(ChunkPosition.of(ore.location())).ores().remove(ore);
    }

    public void load() {
        oreChunkMap.clear();
        for (PlacedOre ore : oreMap.values()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                ore.unrender(player);
            }
            oreMap.remove(ore.entityId());
        }
        for (PlacedOre placedOre : storage.loadAllPlacedOres(new PlacedOreFactory(),
                (name) -> config.types().stream().filter(type -> type.name().equals(name)).findFirst().orElseThrow())) {
            registerPlacedOre(placedOre);
        }
        logger.info("Loaded {} placed ores", oreMap.size());
    }

    public SQLMineStorage storage() {
        return storage;
    }


}
