package it.einjojo.akani.dungeon.lootchest;

import it.einjojo.akani.dungeon.lootchest.bukkit.AsyncLootChestSaveTask;
import it.einjojo.akani.dungeon.lootchest.bukkit.LootChestListener;
import it.einjojo.akani.dungeon.lootchest.bukkit.LootChestTickTask;
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

public class LootChestManager {

    private final JavaPlugin plugin;
    private final LootChestListener lootChestListener;
    private final LootChestTickTask lootChestRenderTask;
    private final List<LootChest> lootChests = new LinkedList<>();
    private final AsyncLootChestSaveTask asyncSaveTask;
    private final LootChestConfig config;
    private final Map<Location, PlacedLootChest> placedLootChestMap = new HashMap<>();

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

    }

    public void save() {

    }

    public void persistPlacedChest(PlacedLootChest lootChest) {
        register(lootChest);
    }

    public void deletePlacedChest(PlacedLootChest lootChest) {

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
        lootChests.add(lootChest);
    }

    public void deleteLootChest(LootChest lootChest) {
        lootChests.remove(lootChest);
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
}
