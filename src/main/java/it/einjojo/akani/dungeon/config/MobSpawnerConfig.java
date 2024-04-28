package it.einjojo.akani.dungeon.config;

import it.einjojo.akani.dungeon.mobs.ChunkPosition;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class MobSpawnerConfig {
    private final ConfigurationSection section;
    private final int selectorInterval;
    private final int spawnerInterval;
    private Map<ChunkPosition, Double> refillOverwrites = new HashMap<>();
    private String worldName;
    private int maxMobsPerChunk;
    private int minMobsPerChunk;

    public MobSpawnerConfig(ConfigurationSection section) {
        this.section = section;
        section.addDefault("world", "world");
        section.addDefault("selectorInterval", 200);
        section.addDefault("spawnerInterval", 2);
        section.addDefault("maxMobsPerChunk", 7);
        section.addDefault("refillOverwrites", Map.of());
        selectorInterval = section.getInt("selectorInterval");
        spawnerInterval = section.getInt("spawnerInterval");
        worldName = section.getString("world");
        maxMobsPerChunk = section.getInt("maxMobsPerChunk");
        minMobsPerChunk = section.getInt("minMobsPerChunk");
        section.getMapList("refillOverwrites").forEach(map -> {
            ChunkPosition chunkPosition = new ChunkPosition((int) map.get("x"), (int) map.get("z"));
            refillOverwrites.put(chunkPosition, (double) map.get("refill"));
        });
    }

    public Map<ChunkPosition, Double> refillOverwrites() {
        return refillOverwrites;
    }

    public void addRefillOverwrite(ChunkPosition chunkPosition, double refill) {
        refillOverwrites.put(chunkPosition, refill);
        setRefillOverwrites(refillOverwrites);
    }

    public void setRefillOverwrites(Map<ChunkPosition, Double> refillOverwrites) {
        this.refillOverwrites = refillOverwrites;
        section.set("refillOverwrites", refillOverwrites.entrySet().stream()
                .map(entry -> Map.of(
                        "x", entry.getKey().x(),
                        "z", entry.getKey().z(),
                        "refill", entry.getValue())
                ).toList());
    }

    public int minMobsPerChunk() {
        return minMobsPerChunk;
    }

    public void setMinMobsPerChunk(int minMobsPerChunk) {
        this.minMobsPerChunk = minMobsPerChunk;
        section.set("minMobsPerChunk", minMobsPerChunk);
    }

    public void setMaxMobsPerChunk(int maxMobsPerChunk) {
        this.maxMobsPerChunk = maxMobsPerChunk;
        section.set("maxMobsPerChunk", maxMobsPerChunk);
    }

    public int maxMobsPerChunk() {
        return maxMobsPerChunk;
    }

    public String worldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
        section.set("world", worldName);
    }

    public int selectorInterval() {
        return selectorInterval;
    }

    public int spawnerInterval() {
        return spawnerInterval;
    }
}
