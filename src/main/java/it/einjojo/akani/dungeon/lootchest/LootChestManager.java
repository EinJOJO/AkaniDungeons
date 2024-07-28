package it.einjojo.akani.dungeon.lootchest;

import it.einjojo.akani.dungeon.lootchest.task.AsyncLootChestSaveTask;
import it.einjojo.akani.dungeon.listener.LootChestListener;
import it.einjojo.akani.dungeon.lootchest.task.LootChestTickTask;
import it.einjojo.akani.dungeon.lootchest.particle.ParticleSpawnerFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LootChestManager implements LootChestChangeObserver {
    private final JavaPlugin plugin;
    private final LootChestListener lootChestListener;
    private final LootChestTickTask lootChestRenderTask;
    private final List<LootChest> lootChests = new LinkedList<>();
    private final AsyncLootChestSaveTask asyncSaveTask;
    private final LootChestConfig config;
    private final Map<Location, PlacedLootChest> placedLootChestMap = new HashMap<>();
    private boolean saveRequired = false;

    public LootChestManager(JavaPlugin plugin, LootChestConfig config) {
        this.plugin = plugin;
        this.config = config;
        asyncSaveTask = new AsyncLootChestSaveTask(this);
        lootChestListener = new LootChestListener(plugin, this);
        lootChestRenderTask = new LootChestTickTask();
    }

    public List<LootChest> lootChests() {
        return lootChests;
    }

    public void startTasks() {
        lootChestRenderTask.start(plugin, config.chestTickRate());
        asyncSaveTask.start(plugin, config.saveTickRate());
    }

    public void load() {
        config.load();
        lootChests.clear();
        lootChests.addAll(config.lootChests());
        for (PlacedLootChest mapValue : placedLootChestMap.values()) {
            lootChestRenderTask.remove(mapValue);
        }
        placedLootChestMap.clear();
        for (PlacedLootChest placedLootChest : config.placedChests()) {
            register(placedLootChest);
        }
    }

    public void save() {
        if (!saveRequired) return;
        config.setLootChests(lootChests);
        config.save();
    }

    public void persistPlacedChest(PlacedLootChest lootChest) {
        register(lootChest);
        config.placedChests().add(lootChest);
        saveRequired = true;
    }

    public void deletePlacedChest(PlacedLootChest lootChest) {
        unregister(lootChest);
        config.placedChests().remove(lootChest);
        saveRequired = true;
    }

    /**
     * Registers a chest to be ticked and rendered but does not save it to file {@link #persistPlacedChest(PlacedLootChest)}
     *
     * @param lootChest The chest to register.
     * @throws IllegalArgumentException If a chest already exists at the location.
     */
    public void register(PlacedLootChest lootChest) {
        if (placedLootChestMap.containsKey(lootChest.location())) {
            throw new IllegalArgumentException("Chest already exists at location " + lootChest.location());
        }
        placedLootChestMap.put(lootChest.location(), lootChest);
        lootChestRenderTask.add(lootChest);
    }

    /**
     * Unregisters a chest from being ticked and rendered. Does not delete the chest from file.
     * {@link #deletePlacedChest(PlacedLootChest)}
     *
     * @param lootChest The chest to unregister.
     */
    public void unregister(PlacedLootChest lootChest) {
        placedLootChestMap.remove(lootChest.location());
        lootChestRenderTask.remove(lootChest);
    }


    public Map<Location, PlacedLootChest> placedLootChestMap() {
        return placedLootChestMap;
    }

    public LootChest createLootChest(String name) {
        return new LootChest(name, Duration.ofMinutes(30), 3, Component.text(name).color(NamedTextColor.GRAY),
                new LinkedList<>(), new ParticleSpawnerFactory().createParticleSpawner("normal"), Material.CHEST);
    }

    public LootChest lootChestByName(String name) {
        for (LootChest lootChest : lootChests) {
            if (lootChest.name().equals(name)) {
                return lootChest;
            }
        }
        return null;
    }

    public PlacedLootChest placedLootChestByLocation(Location location) {
        return placedLootChestMap.get(location);
    }


    public void persistLootChest(LootChest lootChest) {
        if (lootChestByName(lootChest.name()) != null) {
            throw new IllegalArgumentException("Chest with name " + lootChest.name() + " already exists.");
        }
        lootChest.setObserver(this);
        lootChests.add(lootChest);
        saveRequired = true;
    }

    public void deleteLootChest(LootChest lootChest) {
        lootChests.remove(lootChest);
        lootChest.setObserver(null);
        saveRequired = true;
    }

    public JavaPlugin plugin() {
        return plugin;
    }

    public LootChestListener lootChestListener() {
        return lootChestListener;
    }

    public LootChestTickTask lootChestRenderTask() {
        return lootChestRenderTask;
    }

    @Override
    public void onChange(LootChest lootChest) {
        saveRequired = true;
    }
}
