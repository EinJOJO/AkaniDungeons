package it.einjojo.akani.dungeon.gui;

import fr.minuskube.inv.InventoryListener;
import fr.minuskube.inv.SmartInventory;
import it.einjojo.akani.dungeon.config.MineOreTypeConfig;
import it.einjojo.akani.dungeon.mine.MineOreType;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class GuiManager {

    private final MineOreTypeConfig mineOreTypeConfig;
    private final SmartInventory mineOreTypeSelectorGUI;

    public GuiManager(MineOreTypeConfig mineOreTypeConfig) {
        this.mineOreTypeConfig = mineOreTypeConfig;
        this.mineOreTypeSelectorGUI = SmartInventory.builder()
                .id("mineOreTypeSelectorGUI")
                .provider(new MineOreTypeSelectorGUI(mineOreTypeConfig, this))
                .size(6, 9)
                .title("§6Erze")
                .build();
    }

    public SmartInventory mineOreTypeSelectorGUI() {
        return mineOreTypeSelectorGUI;
    }

    public SmartInventory mineOreTypeGUI(MineOreType oreType) {
        MineOreTypeGUI gui = new MineOreTypeGUI(oreType);
        return SmartInventory.builder()
                .id("mineOreTypeGUI_" + oreType.name())
                .provider(gui)
                .parent(mineOreTypeSelectorGUI)
                .title("§6" + oreType.name())
                .size(6, 9)
                .listener(new InventoryListener<>(InventoryCloseEvent.class, e -> {
                    e.getPlayer().sendMessage("§7Erztyp §a" + oreType.name() + "§7 geschlossen.");
                }))
                .build();
    }
}
