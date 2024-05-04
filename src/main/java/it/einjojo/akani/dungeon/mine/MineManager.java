package it.einjojo.akani.dungeon.mine;

import it.einjojo.akani.dungeon.util.ChunkPosition;

import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MineManager {

    private final Map<ChunkPosition, MineChunk> oreChunkMap = new ConcurrentHashMap<>();
    private final Map<Integer, MineOre> oreMap = new ConcurrentHashMap<>();
    private final Map<UUID, MineProgression> progressionMap = new ConcurrentHashMap<>();


    public MineOre oreByEntityId(int entityId) {
        return oreMap.get(entityId);
    }

    public MineChunk chunkByPosition(ChunkPosition position) {
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


    public void registerOre(MineOre ore) {
        oreMap.put(ore.entityId(), ore);
        MineChunk chunk = oreChunkMap.computeIfAbsent(ChunkPosition.of(ore.location()), chunkPosition -> new MineChunk(chunkPosition, new LinkedList<>()));
        chunk.ores().add(ore);
    }


}
