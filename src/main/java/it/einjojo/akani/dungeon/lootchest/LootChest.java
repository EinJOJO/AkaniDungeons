package it.einjojo.akani.dungeon.lootchest;

import it.einjojo.akani.dungeon.util.ItemReward;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class LootChest {
    private static final Random RANDOM = new Random();
    private final String name;
    private final List<ItemReward> potentialRewards;
    private Component displayName;

    public LootChest(String name, Component displayName, List<ItemReward> potentialRewards) {
        this.name = name;
        this.displayName = displayName;
        this.potentialRewards = potentialRewards;
    }

    public String name() {
        return name;
    }

    public List<ItemReward> potentialRewards() {
        return potentialRewards;
    }

    public Component displayName() {
        return displayName;
    }

    public void setDisplayName(Component displayName) {
        this.displayName = displayName;
    }

    public List<ItemStack> generateRandomLoot() {
        List<ItemStack> loot = new LinkedList<>();
        for (ItemReward itemReward : potentialRewards) {
            if (RANDOM.nextDouble() < itemReward.chance()) {
                continue;
            }
            ItemStack itemStack = itemReward.baseItem().clone();
            int amount = RANDOM.nextInt(itemReward.max() - itemReward.min()) + itemReward.min();
            itemStack.setAmount(amount);
            loot.add(itemStack);
        }
        return loot;
    }
}
