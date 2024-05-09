package it.einjojo.akani.dungeon.mine;

import it.einjojo.akani.dungeon.config.PlacedOreConfig;
import it.einjojo.akani.dungeon.util.ChunkPosition;
import it.einjojo.akani.dungeon.util.RepeatingTask;
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

public class MineManager implements RepeatingTask {

    private static final Logger logger = LoggerFactory.getLogger(MineManager.class);
    private final Map<ChunkPosition, MineChunk> oreChunkMap = new ConcurrentHashMap<>();
    private final Map<Integer, PlacedOre> oreMap = new ConcurrentHashMap<>();
    private final Map<UUID, MineProgression> progressionMap = new ConcurrentHashMap<>();
    private final PlacedOreConfig config;
    private BukkitTask saveTask;
    private boolean oreChanged = false;

    public MineManager(PlacedOreConfig config) {
        this.config = config;
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

    public void registerOre(PlacedOre ore) {
        oreMap.put(ore.entityId(), ore);
        MineChunk chunk = oreChunkMap.computeIfAbsent(ChunkPosition.of(ore.location()), chunkPosition -> new MineChunk(chunkPosition, new LinkedList<>()));
        chunk.ores().add(ore);
        oreChanged = true;
        logger.debug("Registered ore {}", ore.entityId());
    }

    public void unregisterOre(PlacedOre ore) {
        oreMap.remove(ore.entityId());
        oreChunkMap.get(ChunkPosition.of(ore.location())).ores().remove(ore);
        oreChanged = true;
    }

    public void save() {
        if (!oreChanged) {
            return;
        }
        config.save(oreMap.values());
        logger.info("Saved {} placed ores", oreMap.size());
    }

    public void load() {
        oreChunkMap.clear();
        for (PlacedOre ore : oreMap.values()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                ore.unrender(player);
            }
            oreMap.remove(ore.entityId());
        }
        config.load().forEach(this::registerOre);
        logger.info("Loaded {} placed ores", oreMap.size());
    }


    @Override
    public BukkitTask task() {
        return saveTask;
    }

    @Override
    public void setTask(BukkitTask task) {
        saveTask = task;
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void run() {
        save();

    }
}
