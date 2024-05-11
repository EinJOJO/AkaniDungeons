package it.einjojo.akani.dungeon.gui;

import co.aikar.commands.annotation.Default;
import fr.minuskube.inv.InventoryListener;
import fr.minuskube.inv.SmartInventory;
import it.einjojo.akani.dungeon.AkaniDungeonPlugin;
import it.einjojo.akani.dungeon.config.MineOreTypeConfig;
import it.einjojo.akani.dungeon.gui.lootchest.LootChestOverviewGui;
import it.einjojo.akani.dungeon.gui.mineoretype.MineOreTypeSelectorGUI;
import it.einjojo.akani.dungeon.gui.mineoretype.MineOreTypeSettingGUI;
import it.einjojo.akani.dungeon.mine.MineOreType;
import it.einjojo.akani.dungeon.mine.factory.MineOreTypeFactory;
import org.bukkit.event.inventory.InventoryCloseEvent;
@Deprecated
public class GuiManager {
    private final AkaniDungeonPlugin plugin;
    private final MineOreTypeConfig mineOreTypeConfig;
    private final SmartInventory mineOreTypeSelectorGUI;
    private final MineOreTypeFactory mineOreTypeFactory;
    private final SmartInventory lootChestOverviewGui;
    @Deprecated
    public GuiManager(AkaniDungeonPlugin plugin, MineOreTypeConfig mineOreTypeConfig, MineOreTypeFactory mineOreTypeFactory) {
        this.plugin = plugin;
        this.mineOreTypeFactory = mineOreTypeFactory;
        this.mineOreTypeConfig = mineOreTypeConfig;
        this.mineOreTypeSelectorGUI = SmartInventory.builder()
                .id("mineOreTypeSelectorGUI")
                .provider(new MineOreTypeSelectorGUI(mineOreTypeConfig, mineOreTypeFactory, this))
                .size(6, 9)
                .title("§6Erze")
                .build();
        this.lootChestOverviewGui = SmartInventory.builder()
                .id("lootChestOverviewGui")
                .provider(new LootChestOverviewGui(plugin.akaniDungeon().lootChestManager()))
                .size(6, 9)
                .title("§6Lootkisten")
                .build();
    }


    @Deprecated
    public SmartInventory mineOreTypeSelectorGUI() {
        return mineOreTypeSelectorGUI;
    }
    @Deprecated
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
