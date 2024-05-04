package it.einjojo.akani.dungeon.gui;

import fr.minuskube.inv.SmartInventory;
import it.einjojo.akani.dungeon.config.MineOreTypeConfig;
import net.kyori.adventure.text.Component;

public class GuiManager {

    private final SmartInventory mineOreTypeSelectorGUI;

    public GuiManager(MineOreTypeConfig mineOreTypeConfig) {
        this.mineOreTypeSelectorGUI = SmartInventory.builder()
                .id("mineOreTypeSelectorGUI")
                .provider(new MineOreTypeSelectorGUI(mineOreTypeConfig))
                .size(6, 9)
                .title("§6Erze")
                .build();
    }

    public SmartInventory mineOreTypeSelectorGUI() {
        return mineOreTypeSelectorGUI;
    }
}
