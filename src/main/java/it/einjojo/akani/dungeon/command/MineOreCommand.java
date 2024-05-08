package it.einjojo.akani.dungeon.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import it.einjojo.akani.dungeon.AkaniDungeon;
import it.einjojo.akani.dungeon.gui.GuiManager;
import it.einjojo.akani.dungeon.mine.BreakReward;
import it.einjojo.akani.dungeon.mine.Hardness;
import it.einjojo.akani.dungeon.mine.MineOreType;
import it.einjojo.akani.dungeon.mine.tool.ToolType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

@CommandAlias("adminmine|amine")
@CommandPermission("akani.mine.admin")
public class MineOreCommand extends BaseCommand {
    @Dependency
    private AkaniDungeon core;
    @Dependency
    private GuiManager guiManager;


    @Subcommand("gui")
    @Description("Open the mine ore type selector GUI.")
    public void openTypesGui(Player sender) {
        guiManager.mineOreTypeSelectorGUI().open(sender);
    }

    @Subcommand("create")
    @CommandCompletion("<erz-name>")
    @Description("Create a mine ore type.")
    public void createType(Player sender, String name) {
        ItemStack itemInHand = sender.getInventory().getItemInMainHand().clone();
        if (itemInHand.getType().equals(Material.AIR)) {
            sender.sendMessage("§cHalte das Erz in der Hand, was der Spieler sehen soll.");
            return;
        }
        itemInHand.setAmount(1);
        MineOreType type = new MineOreType(name, itemInHand, new ArrayList<>(), Hardness.UNDETERMINED, 10, ToolType.PICKAXE);
        core.config().mineOreTypeConfig().addOreType(type);
        core.config().mineOreTypeConfig().save();
        sender.getInventory().addItem(type.spawnEggItemStack());
        sender.sendMessage("§7Das Erz §a" + name + "§7 wurde erstellt.");
    }

    @Subcommand("addreward")
    @CommandCompletion("@oreTypes <min>|1 <max>|3 <chance>|0.01|0.2|0.5|1.0 @nothing")
    @Description("Add a reward to a mine ore type.")
    public void addReward(Player sender, MineOreType type, short min, short max, float chance) {
        ItemStack itemInHand = sender.getInventory().getItemInMainHand().clone();
        if (itemInHand.getType().equals(Material.AIR)) {
            sender.sendMessage("§cHalte das Item in der Hand, was als Belohnung hinzugefügt werden soll.");
            return;
        }
        itemInHand.setAmount(1);
        BreakReward reward = new BreakReward(itemInHand, min, max, chance);
        type.breakRewards().add(reward);
        core.config().mineOreTypeConfig().save();
        sender.sendMessage("§7Abbaubelohnung erstellt für §e" + type.name() + "§8: §7" + reward.baseItem().getType().name() + " " + min + "-" + max + " " + chance);
    }

    @Subcommand("listrewards")
    @Description("List the rewards of a mine ore type.")
    @CommandCompletion("@oreTypes")
    public void listRewards(Player sender, MineOreType type) {
        sender.sendMessage("§7Abbaubelohnungen für §e" + type.name() + "§8:");
        for (BreakReward reward : type.breakRewards()) {
            sender.sendMessage("§7- §e" + reward.baseItem().getType().name() + " " + reward.min() + "-" + reward.max() + " " + reward.chance());
        }
    }

    @Subcommand("sethardness")
    @CommandCompletion("@oreTypes @oreHardness")
    @Description("Set the hardness of a mine ore type.")
    public void setHardness(Player sender, MineOreType oreType, Hardness hardness) {
        oreType.setHardness(hardness);
        core.config().mineOreTypeConfig().save();
        sender.sendMessage("§7Erzhärte überschrieben §e" + oreType.name() + "§8: §7" + hardness.name());
    }

    @Subcommand("sethp")
    @CommandCompletion("@oreTypes <hp>")
    @Description("Set the health points of a mine ore type.")
    public void setHp(Player sender, MineOreType oreType, float hp) {
        oreType.setMaxHealth(hp);
        core.config().mineOreTypeConfig().save();
        sender.sendMessage("§7Erz-HP überschrieben §e" + oreType.name() + "§8: §7" + hp);
    }


}
