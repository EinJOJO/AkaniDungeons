package it.einjojo.akani.dungeon.lootchest;

import it.einjojo.akani.dungeon.lootchest.handler.DefaultPlacedChestHandler;
import it.einjojo.akani.dungeon.lootchest.handler.IDefaultPlacedChestHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.util.HashSet;
import java.util.PriorityQueue;

public class PlacedLootChestFactory {
    private static final BlockData CHEST_BLOCKDATA = Material.CHEST.createBlockData();
    private final IDefaultPlacedChestHandler defaultHandler = new DefaultPlacedChestHandler();

    public PlacedLootChest createSimplePlacedLootChest(LootChest lootChest, Location location) {
        return createDefaultPlacedLootChest(lootChest, location, defaultHandler);
    }

    public PlacedLootChest createDefaultPlacedLootChest(LootChest lootChest, Location location, IDefaultPlacedChestHandler handler) {
        location.getBlock().setBlockData(CHEST_BLOCKDATA);
        return new DefaultPlacedLootChest(lootChest, new HashSet<>(), new HashSet<>(), new PriorityQueue<>(), location, handler);
    }


}
