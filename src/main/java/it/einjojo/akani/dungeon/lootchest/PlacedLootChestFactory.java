package it.einjojo.akani.dungeon.lootchest;

import com.google.common.base.Preconditions;
import it.einjojo.akani.dungeon.AkaniDungeonPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.PriorityQueue;

public class PlacedLootChestFactory {
    private static final BlockData CHEST_BLOCKDATA = Material.CHEST.createBlockData();


    public PlacedLootChest createSimplePlacedLootChest(@NotNull LootChest lootChest, @NotNull Location location) {
        Preconditions.checkNotNull(lootChest, "lootChest cannot be null");
        Preconditions.checkNotNull(location, "location cannot be null");
        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(javaPlugin(), () -> {
                location.getBlock().setBlockData(CHEST_BLOCKDATA);
            });
        } else {
            location.getBlock().setBlockData(CHEST_BLOCKDATA);
        }
        return new DefaultPlacedLootChest(lootChest, new HashSet<>(), new HashSet<>(), new PriorityQueue<>(), location, location.clone().toCenterLocation());
    }

    private JavaPlugin javaPlugin() {
        return AkaniDungeonPlugin.get();
    }


}
