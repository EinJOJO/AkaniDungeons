package it.einjojo.akani.dungeon.gui;

import fr.minuskube.inv.InventoryListener;
import fr.minuskube.inv.SmartInventory;
import it.einjojo.akani.dungeon.config.MineOreTypeConfig;
import it.einjojo.akani.dungeon.gui.mineoretype.MineOreTypeSelectorGUI;
import it.einjojo.akani.dungeon.gui.mineoretype.MineOreTypeSettingGUI;
import it.einjojo.akani.dungeon.mine.MineOreType;
import it.einjojo.akani.dungeon.mine.factory.MineOreTypeFactory;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class GuiManager {
    private final JavaPlugin plugin;
    private final MineOreTypeConfig mineOreTypeConfig;
    private final SmartInventory mineOreTypeSelectorGUI;
    private final MineOreTypeFactory mineOreTypeFactory;

    public GuiManager(JavaPlugin plugin, MineOreTypeConfig mineOreTypeConfig, MineOreTypeFactory mineOreTypeFactory) {
        this.plugin = plugin;
        this.mineOreTypeConfig = mineOreTypeConfig;
        this.mineOreTypeSelectorGUI = SmartInventory.builder()
                .id("mineOreTypeSelectorGUI")
                .provider(new MineOreTypeSelectorGUI(mineOreTypeConfig, mineOreTypeFactory, this))
                .size(6, 9)
                .title("§6Erze")
                .build();
        this.mineOreTypeFactory = mineOreTypeFactory;
    }

    public SmartInventory mineOreTypeSelectorGUI() {
        return mineOreTypeSelectorGUI;
    }

    public SmartInventory mineOreTypeGUI(MineOreType oreType) {
        MineOreTypeSettingGUI gui = new MineOreTypeSettingGUI(oreType, this, plugin);
        return SmartInventory.builder()
                .id("mineOreTypeGUI_" + oreType.name())
                .provider(gui)
                .parent(mineOreTypeSelectorGUI)
                .title("§6" + oreType.name())
                .size(6, 9)
                .listener(new InventoryListener<>(InventoryCloseEvent.class, e -> {
                    plugin.getServer().getScheduler().runTaskAsynchronously(plugin, mineOreTypeConfig::save);
                    e.getPlayer().sendMessage("§7Erztyp §a" + oreType.name() + "§7 geschlossen und gespeichert.");
                }))
                .build();
    }
}
