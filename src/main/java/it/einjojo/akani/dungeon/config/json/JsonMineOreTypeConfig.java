package it.einjojo.akani.dungeon.config.json;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.einjojo.akani.dungeon.config.MineOreTypeConfig;
import it.einjojo.akani.dungeon.mine.MineOreType;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JsonMineOreTypeConfig implements MineOreTypeConfig {
    private final Gson gson;
    private final Path filePath;
    private final List<MineOreType> oreTypes = new ArrayList<>();

    public JsonMineOreTypeConfig(Gson gson, Path filePath) {
        this.gson = gson;
        this.filePath = filePath;
    }

    public void load() {
        oreTypes.clear();
        try {
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
            JsonObject json = gson.fromJson(Files.newBufferedReader(filePath), JsonObject.class);
            if (json == null) return;
            json.getAsJsonArray().forEach(jsonElement -> {
                oreTypes.add(gson.fromJson(jsonElement, MineOreType.class));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addOreType(MineOreType oreType) {
        oreTypes.add(oreType);
    }

    public void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            gson.toJson(oreTypes, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeOreType(MineOreType remove) {
        oreTypes.remove(remove);
    }

    @Override
    public List<MineOreType> types() {
        return List.of();
    }
}
