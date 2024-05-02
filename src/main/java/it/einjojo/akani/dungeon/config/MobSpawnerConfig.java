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

    public static class Dummy implements MobSpawnerConfig {

        @Override
        public Map<ChunkPosition, Double> refillOverwrites() {
            return Map.of();
        }

        @Override
        public void addRefillOverwrite(ChunkPosition chunkPosition, double refill) {

        }

        @Override
        public void setRefillOverwrites(Map<ChunkPosition, Double> refillOverwrites) {

        }

        @Override
        public int minMobsPerChunk() {
            return 0;
        }

        @Override
        public void setMinMobsPerChunk(int minMobsPerChunk) {

        }

        @Override
        public void setMaxMobsPerChunk(int maxMobsPerChunk) {

        }

        @Override
        public int maxMobsPerChunk() {
            return 0;
        }

        @Override
        public String worldName() {
            return "";
        }

        @Override
        public void setWorldName(String worldName) {

        }

        @Override
        public int selectorInterval() {
            return 200;
        }

        @Override
        public int spawnerInterval() {
            return 20;
        }
    }
}
