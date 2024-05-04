package it.einjojo.akani.dungeon.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import it.einjojo.akani.core.paper.util.ItemBuilder;
import it.einjojo.akani.dungeon.config.MineOreTypeConfig;
import it.einjojo.akani.dungeon.mine.MineOreType;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;

public class MineOreTypeSelectorGUI implements InventoryProvider {
    private final MineOreTypeConfig config;


    public MineOreTypeSelectorGUI(MineOreTypeConfig config) {
        this.config = config;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillRow(0, GUIItem.BACKGROUND.emptyClickableItem());
        contents.fillRow(5, GUIItem.BACKGROUND.emptyClickableItem());
        for (MineOreType oreType : config.types()) {
            ClickableItem item = ClickableItem.of(new ItemBuilder(oreType.icon()).lore(List.of(
                    Component.text("§7Rechtsklicke um ein Spawn-Ei für " + oreType.name() + " zuerhalten.")
            )).build(), (e) -> {
                player.getInventory().addItem(oreType.spawnEggItemStack());
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
