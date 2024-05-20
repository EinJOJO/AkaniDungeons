package it.einjojo.akani.dungeon.config.json;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.einjojo.akani.dungeon.config.MobSpawnerConfig;
import it.einjojo.akani.dungeon.util.ChunkPosition;
import org.bukkit.block.Biome;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JsonMobSpawnerConfig implements MobSpawnerConfig {
    private final Path path;
    private final Gson gson;
    private final Map<Biome, List<String>> biomeAssignments = new HashMap<>();
    private Map<ChunkPosition, Double> refillOverwrites = new HashMap<>();
    private int minMobsPerChunk = 1;
    private int maxMobsPerChunk = 6;
    private String worldName = "dungeonn";
    private int selectorInterval = 200;
    private int spawnerInterval = 20;

    public JsonMobSpawnerConfig(Path path, Gson gson) {
        this.path = path;
        this.gson = gson;
    }

    @Override
    public void addMobId(Biome biome, String mobId) {
        biomeAssignments.computeIfAbsent(biome, k -> new LinkedList<>()).add(mobId);
    }

    @Override
    public void removeMobId(Biome biome, String mobId) {
        var biomeList = biomeAssignments.get(biome);
        if (biomeList != null) {
            biomeList.remove(mobId);
        }
    }

    @Override
    public List<String> mobIds(Biome biome) {
        return biomeAssignments.getOrDefault(biome, List.of());
    }

    @Override
    public void addRefillOverwrite(ChunkPosition chunkPosition, double refill) {
        refillOverwrites.put(chunkPosition, refill);
    }

    @Override
    public void setRefillOverwrites(Map<ChunkPosition, Double> refillOverwrites) {
        this.refillOverwrites = refillOverwrites;

    }

    public boolean load() {
        try {
            if (Files.notExists(path)) {
                Files.createFile(path);
            }
            ;
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                JsonElement json = gson.fromJson(reader, JsonElement.class);
                if (json == null) {
                    return false;
                }
                JsonObject root = json.getAsJsonObject();
                if (root.has("refillOverwrites")) {
                    refillOverwrites.clear();
                    for (JsonElement overwriteElement : root.getAsJsonArray("refillOverwrites")) {
                        JsonObject overwrite = overwriteElement.getAsJsonObject();
                        ChunkPosition chunkPosition = new ChunkPosition(overwrite.get("x").getAsInt(), overwrite.get("z").getAsInt());
                        double refill = overwrite.get("refill").getAsDouble();
                        refillOverwrites.put(chunkPosition, refill);
                    }
                }
                if (root.has("biomeAssignments")) {
                    biomeAssignments.clear();
                    for (Map.Entry<String, JsonElement> biomeEntry : root.getAsJsonObject("biomeAssignments").entrySet()) {
                        Biome biome = Biome.valueOf(biomeEntry.getKey());
                        List<String> mobIds = new LinkedList<>();
                        for (JsonElement mobIdElement : biomeEntry.getValue().getAsJsonArray()) {
                            mobIds.add(mobIdElement.getAsString());
                        }
                        biomeAssignments.put(biome, mobIds);
                    }
                }
                if (root.has("minMobsPerChunk")) {
                    minMobsPerChunk = root.get("minMobsPerChunk").getAsInt();
                }
                if (root.has("maxMobsPerChunk")) {
                    maxMobsPerChunk = root.get("maxMobsPerChunk").getAsInt();
                }
                if (root.has("worldName")) {
                    worldName = root.get("worldName").getAsString();
                }
                if (root.has("selectorInterval")) {
                    selectorInterval = root.get("selectorInterval").getAsInt();
                }
                if (root.has("spawnerInterval")) {
                    spawnerInterval = root.get("spawnerInterval").getAsInt();
                }
            }
            return true;
        } catch (IOException ex) {
            ex.fillInStackTrace();
            return false;
        }
    }

    public boolean save() {
        JsonObject root = new JsonObject();
        JsonArray refillOverwrites = new JsonArray();
        for (Map.Entry<ChunkPosition, Double> overwrite : this.refillOverwrites.entrySet()) {
            JsonObject overwriteJson = new JsonObject();
            overwriteJson.addProperty("x", overwrite.getKey().x());
            overwriteJson.addProperty("z", overwrite.getKey().z());
            overwriteJson.addProperty("refill", overwrite.getValue());
            refillOverwrites.add(overwriteJson);
        }
        root.add("refillOverwrites", refillOverwrites);
        JsonObject biomeAssignmentsJson = new JsonObject();
        for (Map.Entry<Biome, List<String>> biomeEntry : this.biomeAssignments.entrySet()) {
            List<String> mobIds = biomeEntry.getValue();
            JsonElement mobIdsJson = gson.toJsonTree(mobIds);
            biomeAssignmentsJson.add(biomeEntry.getKey().name(), mobIdsJson);
        }
        root.add("biomeAssignments", biomeAssignmentsJson);
        root.addProperty("minMobsPerChunk", minMobsPerChunk);
        root.addProperty("maxMobsPerChunk", maxMobsPerChunk);
        root.addProperty("worldName", worldName);
        root.addProperty("selectorInterval", selectorInterval);
        root.addProperty("spawnerInterval", spawnerInterval);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            gson.toJson(root, writer);
            return true;
        } catch (IOException ex) {
            ex.fillInStackTrace();
            return false;
        }
    }


    // GETTER SETTER
    @Override
    public Map<ChunkPosition, Double> refillOverwrites() {
        return refillOverwrites;
    }

    @Override
    public Map<Biome, List<String>> biomeAssignments() {
        return biomeAssignments;
    }

    @Override
    public int minMobsPerChunk() {
        return minMobsPerChunk;
    }

    @Override
    public void setMinMobsPerChunk(int minMobsPerChunk) {
        this.minMobsPerChunk = minMobsPerChunk;
    }

    @Override
    public int maxMobsPerChunk() {
        return maxMobsPerChunk;
    }

    @Override
    public void setMaxMobsPerChunk(int maxMobsPerChunk) {
        this.maxMobsPerChunk = maxMobsPerChunk;
    }

    @Override
    public String worldName() {
        return worldName;
    }

    @Override
    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    @Override
    public int selectorInterval() {
        return selectorInterval;
    }

    public void setSelectorInterval(int selectorInterval) {
        this.selectorInterval = selectorInterval;
    }

    @Override
    public int spawnerInterval() {
        return spawnerInterval;
    }

    public void setSpawnerInterval(int spawnerInterval) {
        this.spawnerInterval = spawnerInterval;
    }
}
