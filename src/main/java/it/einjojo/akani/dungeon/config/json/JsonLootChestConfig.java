package it.einjojo.akani.dungeon.config.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.einjojo.akani.core.paper.util.TextUtil;
import it.einjojo.akani.dungeon.lootchest.LootChest;
import it.einjojo.akani.dungeon.lootchest.LootChestConfig;
import it.einjojo.akani.dungeon.lootchest.PlacedLootChest;
import it.einjojo.akani.dungeon.lootchest.PlacedLootChestFactory;
import it.einjojo.akani.dungeon.lootchest.particle.ParticleSpawner;
import it.einjojo.akani.dungeon.lootchest.particle.ParticleSpawnerFactory;
import it.einjojo.akani.dungeon.util.ItemReward;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

public class JsonLootChestConfig implements LootChestConfig {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final Logger log = LoggerFactory.getLogger(JsonLootChestConfig.class);
    private final Path filePath;
    private final Gson gson;
    private int chestTickRate = 5;
    private int saveTickRate = 300;
    private List<PlacedLootChest> placedChests = List.of();
    private List<LootChest> lootChests = List.of();

    public JsonLootChestConfig(Path filePath, Gson gson) {
        this.filePath = filePath;
        this.gson = gson;
    }


    @Override
    public int chestTickRate() {
        return chestTickRate;
    }

    @Override
    public int saveTickRate() {
        return saveTickRate;
    }

    @Override
    public @NotNull List<PlacedLootChest> placedChests() {
        return placedChests;
    }

    @Override
    public void setPlacedChests(@Nullable List<PlacedLootChest> chests) {
        placedChests = chests;
    }

    @Override
    public @NotNull List<LootChest> lootChests() {
        return lootChests;
    }

    @Override
    public void setLootChests(@Nullable List<LootChest> chests) {
        lootChests = chests;
    }

    @Override
    public boolean load() {
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            chestTickRate = json.get("chestTickRate").getAsInt();
            saveTickRate = json.get("saveTickRate").getAsInt();
            List<LootChest> lootChestList = new LinkedList<>();
            ParticleSpawnerFactory particleSpawnerFactory = new ParticleSpawnerFactory();
            for (JsonElement lootChestJson : json.getAsJsonArray("lootChests")) {
                LootChest created = lootChestFromJson(lootChestJson.getAsJsonObject(), particleSpawnerFactory);
                if (created != null) {
                    lootChestList.add(created);
                }
            }
            setLootChests(lootChestList);
            List<PlacedLootChest> placedLootChestList = new LinkedList<>();
            PlacedLootChestFactory plcFactory = new PlacedLootChestFactory();
            for (JsonElement placedLootChestJson : json.getAsJsonArray("placedChests")) {
                PlacedLootChest created = placedLootChestFromJson(placedLootChestJson.getAsJsonObject(), plcFactory);
                if (created != null) {
                    placedLootChestList.add(created);
                }
            }
            setPlacedChests(placedLootChestList);
            return true;
        } catch (IOException e) {
            e.fillInStackTrace();
            return false;
        }
    }

    private PlacedLootChest placedLootChestFromJson(JsonObject json, PlacedLootChestFactory factory) {
        try {
            String chestName = json.get("chestName").getAsString();
            JsonObject locationObject = json.getAsJsonObject("location");
            double x = locationObject.get("x").getAsDouble();
            double y = locationObject.get("y").getAsDouble();
            double z = locationObject.get("z").getAsDouble();
            String world = locationObject.get("world").getAsString();
            LootChest lootChest = lootChestByName(chestName);
            if (lootChest == null) {
                log.warn("Failed to load placed loot chest from json because no lootChest with the name {} was found.", chestName);
                return null;
            }
            return factory.createSimplePlacedLootChest(lootChest, new Location(Bukkit.getWorld(world), x, y, z));
        } catch (Exception e) {
            log.warn("Failed to load placed loot chest from json: {}", json, e);
            return null;
        }
    }

    private LootChest lootChestFromJson(JsonObject json, ParticleSpawnerFactory particleSpawnerFactory) {
        try {
            String name = json.get("name").getAsString();
            Component displayName = MINI_MESSAGE.deserialize(TextUtil.transformAmpersandToMiniMessage(json.get("displayName").getAsString()));
            int rows = json.get("rows").getAsInt();
            int lockedMinutes = json.get("lockDurationMinutes").getAsInt();
            List<ItemReward> potentialRewards = new LinkedList<>();
            ParticleSpawner spawner = particleSpawnerFactory.createParticleSpawner(json.get("particleSpawner").getAsString());
            Material guiIconMaterial = Material.valueOf(json.get("guiIconMaterial").getAsString());
            return new LootChest(name, Duration.ofMinutes(lockedMinutes), rows, displayName, potentialRewards, spawner, guiIconMaterial);
        } catch (Exception e) {
            log.warn("Failed to load loot chest from json: {}", json, e);
            return null;
        }

    }

    private JsonObject jsonFromLootChest(LootChest lootChest) {
        JsonObject json = new JsonObject();
        json.addProperty("name", lootChest.name());
        json.addProperty("displayName", MINI_MESSAGE.serialize(lootChest.displayName()));
        json.addProperty("rows", lootChest.slotSize() / 9);
        json.addProperty("lockDurationMinutes", lootChest.lockDuration().toMinutes());
        json.addProperty("particleSpawner", lootChest.particleSpawner().name());
        json.addProperty("guiIconMaterial", lootChest.guiIconMaterial().name());
        return json;
    }

    private LootChest lootChestByName(String name) {
        for (LootChest lootChest : lootChests) {
            if (lootChest.name().equals(name)) {
                return lootChest;
            }
        }
        return null;
    }

    @Override
    public boolean save() {
        return false;
    }
}
