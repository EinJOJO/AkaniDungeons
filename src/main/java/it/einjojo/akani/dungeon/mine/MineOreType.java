package it.einjojo.akani.dungeon.mine;

import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Represents a type of ore that can be found in a mine.
 *
 * @param icon the icon of the ore
 */
public record MineOreType(ItemStack icon, List<BreakReward> breakRewards) {


}
