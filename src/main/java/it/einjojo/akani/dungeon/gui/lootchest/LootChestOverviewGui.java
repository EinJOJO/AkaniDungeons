package it.einjojo.akani.dungeon.gui.lootchest;

import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import it.einjojo.akani.dungeon.lootchest.LootChestManager;
import org.bukkit.entity.Player;

public class LootChestOverviewGui implements InventoryProvider {
    private static SmartInventory instance;
    private final LootChestManager lootChestManager;

    public LootChestOverviewGui(LootChestManager lootChestManager) {
        this.lootChestManager = lootChestManager;
    }

    public static SmartInventory inventory() {
        if (instance == null) {
            instance = SmartInventory.builder()
                    .id("lootChestOverviewGui")
                    .size(6, 9)
                    .title("§6Lootkisten")
                    .build();
        }
        return instance;
    }

    @Override
    public void init(Player player, InventoryContents contents) {

    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
