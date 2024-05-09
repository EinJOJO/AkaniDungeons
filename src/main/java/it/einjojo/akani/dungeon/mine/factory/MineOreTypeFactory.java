package it.einjojo.akani.dungeon.mine.factory;

import it.einjojo.akani.dungeon.mine.Hardness;
import it.einjojo.akani.dungeon.mine.MineOreType;
import it.einjojo.akani.dungeon.mine.tool.ToolType;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.util.LinkedList;

public class MineOreTypeFactory {

    public MineOreType createMineOreType(ItemStack icon) {
        return new MineOreType(icon.getType().name().toLowerCase(), icon, new LinkedList<>(),
                Hardness.UNDETERMINED, 20f, ToolType.PICKAXE, Duration.ofMinutes(30));
    }


}
