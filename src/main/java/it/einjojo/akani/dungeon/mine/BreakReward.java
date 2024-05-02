package it.einjojo.akani.dungeon.mine;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public record BreakReward(ItemStack baseItem, int min, int max, double chance) {
    private static final Random random = new Random();

    public @Nullable ItemStack reward(ItemStack usedTool) {
        if (random.nextDouble() > chance) {
            return null;
        }
        int amount = random.nextInt(max - min) + min;
        ItemStack reward = baseItem.clone();
        reward.setAmount(amount);
        return reward;
    }
}
