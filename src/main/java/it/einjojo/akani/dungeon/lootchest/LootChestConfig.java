package it.einjojo.akani.dungeon.lootchest;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface LootChestConfig {
    int chestTickRate();

    int saveTickRate();

    @NotNull
    List<PlacedLootChest> placedChests();

    void setPlacedChests(@Nullable List<PlacedLootChest> chests);

    @NotNull
    List<LootChest> lootChests();

    void setLootChests(@Nullable List<LootChest> chests);

    boolean load();

    boolean save();


}
