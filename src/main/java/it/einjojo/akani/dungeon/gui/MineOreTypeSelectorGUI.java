package it.einjojo.akani.dungeon.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import it.einjojo.akani.core.paper.util.ItemBuilder;
import it.einjojo.akani.dungeon.config.MineOreTypeConfig;
import it.einjojo.akani.dungeon.mine.MineOreType;
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
        for (MineOreType oreType : config.types()) {
            ClickableItem.of(new ItemBuilder(oreType.icon()).lore(List.of(

            )).build(), (e) -> {

            });

        }
        contents.fillRow(5, GUIItem.BACKGROUND.emptyClickableItem());
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
