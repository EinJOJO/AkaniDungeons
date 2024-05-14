package it.einjojo.akani.dungeon.lootchest;

import it.einjojo.akani.dungeon.util.ItemReward;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class LootChest {
    private static final Random RANDOM = new Random();
    private final String name;
    private final Duration lockDuration;
    private final int slotSize;
    private final List<ItemReward> potentialRewards;
    private Component displayName;

    /**
     * @param name             identifier for the loot chest
     * @param lockDuration     duration the chest is locked for after being opened
     * @param rows             rows to determine size of the chest
     * @param displayName      display name of the chest
     * @param potentialRewards list of potential rewards
     */
    public LootChest(String name, Duration lockDuration, int rows, Component displayName, List<ItemReward> potentialRewards) {

        this.name = name;
        this.lockDuration = lockDuration;
        this.slotSize = 9 * rows;
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

    public Duration lockDuration() {
        return lockDuration;
    }

    public int slotSize() {
        return slotSize;
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
