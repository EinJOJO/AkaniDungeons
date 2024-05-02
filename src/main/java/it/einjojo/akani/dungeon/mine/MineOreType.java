package it.einjojo.akani.dungeon.mine;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a type of ore that can be found in a mine.
 *
 * @param icon the icon of the ore
 */
public record MineOreType(ItemStack icon, List<BreakReward> breakRewards, Hardness hardness) {


    public boolean canBreak(ItemStack tool) {
        return Hardness.canBreak(tool.getType(), hardness);
    }

    public List<ItemStack> breakRewards(ItemStack toolUsed) {
        if (breakRewards == null || breakRewards.isEmpty()) return List.of();
        List<ItemStack> rewards = new ArrayList<>();
        for (BreakReward potentialReward : breakRewards) {
            ItemStack is = potentialReward.reward(toolUsed);
            if (is != null) {
                rewards.add(is);
            }
        }
        return rewards;
    }
}
