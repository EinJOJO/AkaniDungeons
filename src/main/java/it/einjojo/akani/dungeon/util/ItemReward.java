package it.einjojo.akani.dungeon.util;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * Represents a reward that can be obtained by breaking a block.
 *
 * @param baseItem the item that will be given as a reward
 * @param min      the minimum amount of items that can be given
 * @param max      the maximum amount of items that can be given
 * @param chance   the chance of the reward being given
 */
public record ItemReward(ItemStack baseItem, short min, short max, float chance) {
    private static final Random random = new Random();

    public @Nullable ItemStack reward(ItemStack usedTool) {
        if (random.nextDouble() > chance) {
            return null;
        }
        int amount = max == min ? min : random.nextInt(max - min) + min;
        if (amount <= 0) {
            return null;
        }
        ItemStack reward = baseItem.clone();
        reward.setAmount(amount);
        return reward;
    }
}
