package it.einjojo.akani.dungeon.gui;

import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.entity.Player;

public class MineOreTypeSelectorGUI implements InventoryProvider {

    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("mineOreTypeSelector")
            .provider(new MineOreTypeSelectorGUI())
            .size(6, 9)
            .title("Available Ores")
            .build();

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillRow(0, GUIItem.BACKGROUND.emptyClickableItem());
        contents.fillRow(5, GUIItem.BACKGROUND.emptyClickableItem());
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
