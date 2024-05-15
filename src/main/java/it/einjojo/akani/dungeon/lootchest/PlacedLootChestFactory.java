package it.einjojo.akani.dungeon.lootchest;

import com.google.common.base.Preconditions;
import it.einjojo.akani.dungeon.lootchest.handler.DefaultPlacedChestHandler;
import it.einjojo.akani.dungeon.lootchest.handler.IDefaultPlacedChestHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.PriorityQueue;

public class PlacedLootChestFactory {
    private static final BlockData CHEST_BLOCKDATA = Material.CHEST.createBlockData();
    private final IDefaultPlacedChestHandler defaultHandler = new DefaultPlacedChestHandler();

    public PlacedLootChest createSimplePlacedLootChest(@NotNull LootChest lootChest, Location location) {
        return createDefaultPlacedLootChest(lootChest, location, defaultHandler);
    }

    public PlacedLootChest createDefaultPlacedLootChest(@NotNull LootChest lootChest, @NotNull Location location, @NotNull IDefaultPlacedChestHandler handler) {
        Preconditions.checkNotNull(lootChest, "lootChest cannot be null");
        Preconditions.checkNotNull(location, "location cannot be null");
        Preconditions.checkNotNull(handler, "handler cannot be null");
        location.getBlock().setBlockData(CHEST_BLOCKDATA);
        return new DefaultPlacedLootChest(lootChest, new HashSet<>(), new HashSet<>(), new PriorityQueue<>(), location, handler);
    }


}
