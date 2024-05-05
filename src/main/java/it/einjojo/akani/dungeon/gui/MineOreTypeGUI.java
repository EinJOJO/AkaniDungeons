package it.einjojo.akani.dungeon.gui;

import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import it.einjojo.akani.dungeon.AkaniDungeon;
import it.einjojo.akani.dungeon.mine.MineOreType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class MineOreTypeGUI implements InventoryProvider {

    private final MineOreType oreType;

    public MineOreTypeGUI(MineOreType oreType) {
        this.oreType = oreType;
    }

    @Override
    public void init(Player player, InventoryContents contents) {

    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }

}
