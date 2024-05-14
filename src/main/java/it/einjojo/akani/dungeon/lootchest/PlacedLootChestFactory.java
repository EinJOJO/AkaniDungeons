package it.einjojo.akani.dungeon.lootchest;

import it.einjojo.akani.dungeon.lootchest.handler.DefaultPlacedChestHandler;
import it.einjojo.akani.dungeon.lootchest.handler.IDefaultPlacedChestHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.util.HashSet;
import java.util.PriorityQueue;

public class PlacedLootChestFactory {
    private static final BlockData BARRIER_BLOCKDATA = Material.BARRIER.createBlockData();
    private final IDefaultPlacedChestHandler defaultHandler = new DefaultPlacedChestHandler();

    public PlacedLootChest createSimplePlacedLootChest(LootChest lootChest, Location location) {
        return createDefaultPlacedLootChest(lootChest, location, defaultHandler);
    }

    public PlacedLootChest createDefaultPlacedLootChest(LootChest lootChest, Location location, IDefaultPlacedChestHandler handler) {
        placeBarrierBlock(location);
        return new DefaultPlacedLootChest(lootChest, new HashSet<>(), new HashSet<>(), new PriorityQueue<>(), location, handler);
    }

    public void placeBarrierBlock(Location location) {
        location.getBlock().setBlockData(BARRIER_BLOCKDATA);
    }


}
