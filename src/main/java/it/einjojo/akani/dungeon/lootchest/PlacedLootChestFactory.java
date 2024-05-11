package it.einjojo.akani.dungeon.lootchest;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.util.HashSet;
import java.util.PriorityQueue;

public class PlacedLootChestFactory {
    private static final BlockData BARRIER_BLOCKDATA = Material.BARRIER.createBlockData();
    private final PlacedChestObserver observer;

    public PlacedLootChestFactory(PlacedChestObserver observer) {
        this.observer = observer;
    }

    public PlacedLootChest createPlacedLootChest(LootChest lootChest, Location location) {
        return new PlacedLootChest(lootChest, new HashSet<>(), new HashSet<>(), new PriorityQueue<>(), location, observer);
    }


}
