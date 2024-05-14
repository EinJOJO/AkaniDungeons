package it.einjojo.akani.dungeon.lootchest;

import it.einjojo.akani.dungeon.listener.LootChestListener;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.LinkedList;

public class LootChestManager {

    private final JavaPlugin plugin;
    private final LootChestListener lootChestListener;
    private final LootChestTickTask lootChestRenderTask;

    public LootChestManager(JavaPlugin plugin) {
        this.plugin = plugin;
        lootChestListener = new LootChestListener(plugin);
        lootChestRenderTask = new LootChestTickTask();
    }

    public void startTickTask() {
        lootChestRenderTask.start(plugin, 3);
    }

    public void load() {

    }

    public void save() {

    }

    public void persist(PlacedLootChest lootChest) {
        register(lootChest);
    }

    public void delete(PlacedLootChest lootChest) {

    }

    public void register(PlacedLootChest lootChest) {
        lootChestListener.registerPlacedLootChest(lootChest);
        lootChestRenderTask.add(lootChest);
    }

    public void unregister(PlacedLootChest lootChest) {
        lootChestListener.unregisterPlacedLootChest(lootChest);
        lootChestRenderTask.remove(lootChest);
    }

    public LootChest createLootChest(String name) {
        return new LootChest(name, Duration.ofMinutes(30), 9 * 3, Component.text(""), new LinkedList<>());
    }


}
