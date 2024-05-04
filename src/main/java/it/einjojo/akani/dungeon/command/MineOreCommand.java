package it.einjojo.akani.dungeon.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Subcommand;
import it.einjojo.akani.dungeon.AkaniDungeon;
import it.einjojo.akani.dungeon.gui.GuiManager;
import it.einjojo.akani.dungeon.mine.Hardness;
import it.einjojo.akani.dungeon.mine.MineOre;
import it.einjojo.akani.dungeon.mine.MineOreType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

@CommandAlias("mineore|mine")
public class MineOreCommand extends BaseCommand {

    @Dependency
    private AkaniDungeon core;

    @Dependency
    private GuiManager guiManager;

    private MineOre lastOre;


    @Default
    public void openTypesGui(Player sender) {
        guiManager.mineOreTypeSelectorGUI().open(sender);
    }

    @Subcommand("create")
    public void createType(Player sender, String name) {
        ItemStack itemInHand = sender.getInventory().getItemInMainHand();
        if (itemInHand.getType().equals(Material.AIR)) {
            sender.sendMessage("You must be holding an item in your hand to create a mine ore type.");
            return;
        }
        MineOreType type = new MineOreType(name, itemInHand, new ArrayList<>(), Hardness.UNDETERMINED);
        core.config().mineOreTypeConfig().addOreType(type);
        core.config().mineOreTypeConfig().save();
        sender.sendMessage("Mine ore type " + name + " created.");
    }


}
