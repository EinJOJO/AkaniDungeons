package it.einjojo.akani.dungeon.lootchest;

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
    private final Map<Location, PlacedLootChest> placedLootChestMap = new HashMap<>();

    public LootChestManager(JavaPlugin plugin) {
        this.plugin = plugin;
        lootChestListener = new LootChestListener(plugin, this);
        lootChestRenderTask = new LootChestTickTask();
    }

    public List<LootChest> lootChests() {
        return lootChests;
    }

    public void startTickTask() {
        lootChestRenderTask.start(plugin, 3);
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

    public void register(PlacedLootChest lootChest) {
        placedLootChestMap.put(lootChest.location(), lootChest);
        lootChestRenderTask.add(lootChest);
    }

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

    public LootChest chestByName(String name) {
        for (LootChest lootChest : lootChests) {
            if (lootChest.name().equals(name)) {
                return lootChest;
            }
        }
        return null;
    }


    public void persistChest(LootChest lootChest) {
        if (chestByName(lootChest.name()) != null) {
            throw new IllegalArgumentException("Chest with name " + lootChest.name() + " already exists.");
        }
        lootChests.add(lootChest);
    }

    public void deleteChest(LootChest lootChest) {
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
