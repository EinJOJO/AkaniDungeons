package it.einjojo.akani.dungeon.gui.lootchest;

import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotIterator;
import it.einjojo.akani.dungeon.lootchest.LootChest;
import org.bukkit.entity.Player;

public class LootChestItemsGui implements InventoryProvider {
    private final LootChest lootChest;

    public LootChestItemsGui(LootChest lootChest) {
        this.lootChest = lootChest;
    }

    @Override
    public void init(Player player, InventoryContents contents) {

    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
