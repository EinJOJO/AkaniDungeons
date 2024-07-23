package it.einjojo.akani.dungeon.config.json;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import it.einjojo.akani.dungeon.config.MineOreTypeConfig;
import it.einjojo.akani.dungeon.mine.MineOreType;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class JsonMineOreTypeConfig implements MineOreTypeConfig {
    private final Gson gson;
    private final Path filePath;
    private final List<MineOreType> oreTypes = new LinkedList<>();

    public JsonMineOreTypeConfig(Gson gson, Path filePath) {
        this.gson = gson;
        this.filePath = filePath;
    }


    public void load() {
        oreTypes.clear();
        try {
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
                return;
            }
            var json = gson.fromJson(Files.newBufferedReader(filePath), JsonArray.class);
            if (json == null) return;
            json.forEach(jsonElement -> {
                oreTypes.add(gson.fromJson(jsonElement, MineOreType.class));
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addOreType(MineOreType oreType) {
        for (MineOreType type : oreTypes) {
            if (type.name().equals(oreType.name())) {
                throw new IllegalArgumentException("Ore type with name " + oreType.name() + " already exists");
            }
        }
        oreTypes.add(oreType);
    }

    public void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            gson.toJson(oreTypes, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeOreType(MineOreType remove) {
        oreTypes.remove(remove);
    }

    @Override
    public List<MineOreType> types() {
        return oreTypes;
    }
}
