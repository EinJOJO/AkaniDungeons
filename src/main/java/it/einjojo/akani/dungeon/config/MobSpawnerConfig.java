package it.einjojo.akani.dungeon.config;

import it.einjojo.akani.dungeon.util.ChunkPosition;

import java.util.Map;

public interface MobSpawnerConfig {

    Map<ChunkPosition, Double> refillOverwrites();

    void addRefillOverwrite(ChunkPosition chunkPosition, double refill);

    void setRefillOverwrites(Map<ChunkPosition, Double> refillOverwrites);

    int minMobsPerChunk();

    void setMinMobsPerChunk(int minMobsPerChunk);

    void setMaxMobsPerChunk(int maxMobsPerChunk);

    int maxMobsPerChunk();

    String worldName();

    void setWorldName(String worldName);

    int selectorInterval();

    int spawnerInterval();
}
