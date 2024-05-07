package it.einjojo.akani.dungeon.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import it.einjojo.akani.core.paper.util.ItemBuilder;
import it.einjojo.akani.dungeon.config.MineOreTypeConfig;
import it.einjojo.akani.dungeon.mine.MineOreType;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.List;

public class MineOreTypeSelectorGUI implements InventoryProvider {
    private static final NamespacedKey MINE_ORE_TYPE_KEY = new NamespacedKey("akani", "gui_mine_ore_type");
    private final MineOreTypeConfig config;
    private final GuiManager guiManager;


    public MineOreTypeSelectorGUI(MineOreTypeConfig config, GuiManager guiManager) {
        this.config = config;
        this.guiManager = guiManager;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillRow(0, GUIItem.BACKGROUND.emptyClickableItem());
        contents.fillRow(5, GUIItem.BACKGROUND.emptyClickableItem());
        for (int i = 0; i < config.types().size(); i++) {
            MineOreType oreType = config.types().get(i);
            ClickableItem item = ClickableItem.of(new ItemBuilder(oreType.icon()).lore(List.of(
                    Component.empty(),
                    Component.text("§7▶ Name: §c" + oreType.name()),
                    Component.text("§7▶ HP: §c" + oreType.maxHealth() + " ❤"),
                    Component.text("§7▶ Härte: §c" + oreType.hardness().name()),
                    Component.empty(),
                    Component.text("§7[§cLinksklick§7] Einstellungen"),
                    Component.text("§7[§cRechtsklick§7] Spawn-Ei"),
                    Component.empty()

            )).build(), (e) -> {
                if (e.isLeftClick()) {
                    guiManager.mineOreTypeGUI(oreType).open(player);
                } else {
                    player.getInventory().addItem(oreType.spawnEggItemStack());
                }
            });
            contents.add(item);
        }
        contents.set(5, 4, GUIItem.ADD_BUTTON.clickableItem(e -> {

            player.sendMessage(Component.text("§aHalte den Block, der zusehen sein soll, in der Hand und gib ihm einen Namen."));
            player.sendMessage(Component.text("§aVerwende /mineore types create <name> um eine neue Erz-Kategorie zu erstellen."));
            player.closeInventory();

        }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }
}
