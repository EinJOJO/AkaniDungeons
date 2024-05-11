package it.einjojo.akani.dungeon.gui.lootchest;

import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import it.einjojo.akani.dungeon.lootchest.LootChestManager;
import org.bukkit.entity.Player;

public class LootChestOverviewGui implements InventoryProvider {
    private final LootChestManager lootChestManager;

    public LootChestOverviewGui(LootChestManager lootChestManager) {
        this.lootChestManager = lootChestManager;
    }

    @Override
    public void init(Player player, InventoryContents contents) {

    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
