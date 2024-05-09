package it.einjojo.akani.dungeon.config.json;

import com.google.gson.Gson;
import it.einjojo.akani.dungeon.config.PlacedOreConfig;
import it.einjojo.akani.dungeon.mine.PlacedOre;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class JsonPlacedOreConfig implements PlacedOreConfig {
    private final Gson gson;
    private final Path filePath;

    public JsonPlacedOreConfig(Gson gson, Path filePath) {
        this.gson = gson;
        this.filePath = filePath;
    }

    @Override
    public List<PlacedOre> load() {
        try {
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
            }
            var json = gson.fromJson(Files.newBufferedReader(filePath), PlacedOre[].class);
            if (json == null) return List.of();
            return List.of(json);
        } catch (Exception e) {
            e.fillInStackTrace();
            return List.of();
        }
    }

    @Override
    public void save(Collection<PlacedOre> ores) {
        try {
            gson.toJson(ores, Files.newBufferedWriter(filePath));
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }
}
