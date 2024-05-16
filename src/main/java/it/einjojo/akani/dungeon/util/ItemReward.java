package it.einjojo.akani.dungeon.util;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class ItemReward {
    private static final Random random = new Random();
    private ItemStack baseItem;
    private short min;
    private short max;
    private float chance;

    public ItemReward(ItemStack baseItem, short min, short max, float chance) {
        this.baseItem = baseItem;
        this.min = min;
        this.max = max;
        this.chance = chance;
    }

    public ItemStack baseItem() {
        return baseItem.clone();
    }

    public void setBaseItem(ItemStack baseItem) {
        this.baseItem = baseItem;
    }

    public short min() {
        return min;
    }

    public void setMin(short min) {
        if (min > max) setMax(min);
        this.min = min;
    }

    public short max() {
        return max;
    }

    public void setMax(short max) {
        if (max < min) setMin(max);
        this.max = max;
    }

    public float chance() {
        return chance;
    }

    public void setChance(float chance) {
        this.chance = chance;
    }

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
