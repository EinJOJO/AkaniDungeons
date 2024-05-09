package it.einjojo.akani.dungeon.mine;

import it.einjojo.akani.dungeon.util.ChunkPosition;
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
    private boolean oreChanged = false;

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
    }

    public void load() {

    }


}
