package it.einjojo.akani.dungeon.config;

import it.einjojo.akani.dungeon.util.ChunkPosition;
import org.bukkit.block.Biome;

import java.util.List;
import java.util.Map;

public interface MobSpawnerConfig {

    Map<ChunkPosition, Double> refillOverwrites();

    Map<Biome, List<String>> biomeAssignments();

    List<String> mobIds(Biome biome);

    void addMobId(Biome biome, String mobId);

    void removeMobId(Biome biome, String mobId);

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

    boolean load();

    boolean save();

    public static class Dummy implements MobSpawnerConfig {

        @Override
        public void addMobId(Biome biome, String mobId) {

        }

        @Override
        public void removeMobId(Biome biome, String mobId) {

        }

        @Override
        public boolean load() {
            return false;
        }

        @Override
        public boolean save() {
            return false;
        }

        @Override
        public Map<Biome, List<String>> biomeAssignments() {
            return Map.of();
        }

        @Override
        public List<String> mobIds(Biome biome) {
            return List.of();
        }

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
