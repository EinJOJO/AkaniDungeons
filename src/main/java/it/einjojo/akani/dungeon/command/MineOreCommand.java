package it.einjojo.akani.dungeon.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import it.einjojo.akani.dungeon.AkaniDungeon;
import it.einjojo.akani.dungeon.gui.GuiManager;
import it.einjojo.akani.dungeon.mine.BreakReward;
import it.einjojo.akani.dungeon.mine.Hardness;
import it.einjojo.akani.dungeon.mine.MineOreType;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

@CommandAlias("adminmine|amine")
@CommandPermission("akani.mine.admin")
public class MineOreCommand extends BaseCommand {
    @Dependency
    private AkaniDungeon core;
    @Dependency
    private GuiManager guiManager;
    @Dependency
    private JavaPlugin plugin;


    @Subcommand("gui")
    @Description("Open the mine ore type selector GUI.")
    public void openTypesGui(Player sender) {
        guiManager.mineOreTypeSelectorGUI().open(sender);
    }

    @Subcommand("create")
    @Description("Create a mine ore type.")
    @Syntax("")
    public void createType(Player sender) {
        ItemStack itemInHand = sender.getInventory().getItemInMainHand().clone();
        if (itemInHand.getType().equals(Material.AIR)) {
            sender.sendMessage("§cHalte das Erz in der Hand, was der Spieler sehen soll.");
            return;
        }
        itemInHand.setAmount(1);
        MineOreType type = core.mineOreTypeFactory().createMineOreType(itemInHand);
        core.config().mineOreTypeConfig().addOreType(type);
        core.config().mineOreTypeConfig().save();
        sender.getInventory().addItem(type.spawnEggItemStack());
        sender.sendMessage("§7Das Erz §a" + type.name() + "§7 wurde erstellt.");
    }

    @Subcommand("save")
    @Syntax("")
    @Description("Save the placed mine ore types.")
    public void savePlaced(Player sender) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            core.mineManager().save();
            sender.sendMessage("§7Platzierte Erze gespeichert.");
        });
    }

    @Subcommand("reload|rl")
    @Syntax("")
    @Description("Reload the mine ore types and placed ores.")
    public void reload(CommandSender sender) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            core.config().mineOreTypeConfig().load();
            core.mineManager().load();
            sender.sendMessage("§7Erze und platzierte Erze neu geladen.");
        });
    }


    @Subcommand("addreward")
    @CommandCompletion("@oreTypes <min>|1 <max>|3 <chance>|0.01|0.2|0.5|1.0 @nothing")
    @Syntax("<oreType> <min> <max> <chance>")
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
    @Syntax("<oreType>")
    @CommandCompletion("@oreTypes")
    public void listRewards(Player sender, MineOreType type) {
        sender.sendMessage("§7Abbaubelohnungen für §e" + type.name() + "§8:");
        for (BreakReward reward : type.breakRewards()) {
            sender.sendMessage("§7- §e" + reward.baseItem().getType().name() + " " + reward.min() + "-" + reward.max() + " " + reward.chance());
        }
    }

    @Subcommand("sethardness")
    @CommandCompletion("@oreTypes @oreHardness")
    @Syntax("<oreType> <hardness>")
    @Description("Set the hardness of a mine ore type.")
    public void setHardness(Player sender, MineOreType oreType, Hardness hardness) {
        oreType.setHardness(hardness);
        core.config().mineOreTypeConfig().save();
        sender.sendMessage("§7Erzhärte überschrieben §e" + oreType.name() + "§8: §7" + hardness.name());
    }

    @Subcommand("sethp")
    @CommandCompletion("@oreTypes <hp>")
    @Syntax("<oreType> <hp>")
    @Description("Set the health points of a mine ore type.")
    public void setHp(Player sender, MineOreType oreType, float hp) {
        oreType.setMaxHealth(hp);
        core.config().mineOreTypeConfig().save();
        sender.sendMessage("§7Erz-HP überschrieben §e" + oreType.name() + "§8: §7" + hp);
    }

    @Default
    @HelpCommand
    public void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

}
