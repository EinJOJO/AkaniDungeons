package it.einjojo.akani.dungeon.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Subcommand;
import it.einjojo.akani.dungeon.AkaniDungeon;
import it.einjojo.akani.dungeon.mine.MineOre;
import it.einjojo.akani.dungeon.mine.MineOreType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandAlias("mineore")
public class MineOreCommand extends BaseCommand {

    @Dependency
    private AkaniDungeon core;

    private MineOre lastOre;

    @Subcommand("spawn")
    public void spawn(Player sender) {
        ItemStack itemInHand = sender.getInventory().getItemInMainHand();
        if (itemInHand.getType().equals(Material.AIR)) {
            sender.sendMessage("You must be holding an item in your hand to spawn a mine ore.");
            return;
        }
        if (lastOre != null) {
            lastOre.unrender(sender);
        }
        lastOre = core.mineOreFactory().createMineOre(sender.getLocation(), new MineOreType(itemInHand, null));
        lastOre.render(sender);
    }


}
