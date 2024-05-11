package it.einjojo.akani.dungeon.gui.lootchest;

import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import it.einjojo.akani.dungeon.lootchest.LootChest;
import org.bukkit.entity.Player;

public class LootChestSettingsGui implements InventoryProvider {

    private final LootChest lootChest;

    public LootChestSettingsGui(LootChest lootChest) {
        this.lootChest = lootChest;
    }

    public static SmartInventory inventory(LootChest lootChest) {
        return SmartInventory.builder()
                .id("lootChestSettingsGui")
                .provider(new LootChestSettingsGui(lootChest))
                .size(6, 9)
                .title("§6Lootkiste")
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {

    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
