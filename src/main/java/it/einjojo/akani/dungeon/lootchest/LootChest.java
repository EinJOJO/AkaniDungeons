package it.einjojo.akani.dungeon.lootchest;

import it.einjojo.akani.core.paper.util.ItemBuilder;
import it.einjojo.akani.dungeon.lootchest.particle.ParticleSpawner;
import it.einjojo.akani.dungeon.util.ItemReward;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class LootChest {
    public static final NamespacedKey CHEST_PLACER_ITEM_KEY = new NamespacedKey("akani", "lootchest");
    private static final Random RANDOM = new Random();
    private final String name;
    private final List<ItemReward> potentialRewards;
    private Material guiIconMaterial;
    private int slotSize;
    private Duration lockDuration;
    private ParticleSpawner particleSpawner;
    private Component displayName;
    private transient LootChestChangeObserver observer;


    /**
     * @param name             identifier for the loot chest
     * @param lockDuration     duration the chest is locked for after being opened
     * @param rows             rows to determine size of the chest
     * @param displayName      display name of the chest
     * @param potentialRewards list of potential rewards
     */
    public LootChest(String name, Duration lockDuration, int rows, Component displayName, List<ItemReward> potentialRewards, ParticleSpawner particleSpawner, Material guiIconMaterial) {
        this.name = name;
        this.lockDuration = lockDuration;
        this.slotSize = 9 * rows;
        this.displayName = displayName;
        this.potentialRewards = potentialRewards;
        this.particleSpawner = particleSpawner;
        this.guiIconMaterial = guiIconMaterial;
    }

    public static Optional<String> chestTypeNameFromItemStack(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return Optional.empty();
        }
        if (itemStack.hasItemMeta()) {
            return Optional.ofNullable(itemStack.getItemMeta().getPersistentDataContainer().get(CHEST_PLACER_ITEM_KEY, PersistentDataType.STRING));
        }
        return Optional.empty();
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
        callChangeObserver();
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
            if (RANDOM.nextDouble() > itemReward.chance()) {
                continue;
            }
            ItemStack itemStack = itemReward.baseItem();
            int bound = itemReward.max() - itemReward.min();
            int amount = bound <= 0 ? itemReward.min() : RANDOM.nextInt(bound) + itemReward.min();
            itemStack.setAmount(amount);
            loot.add(itemStack);
        }
        return loot;
    }

    public ItemStack chestPlacerItem() {
        return new ItemBuilder(guiIconMaterial)
                .lore(List.of(Component.text("§7Rechtsklick um eine Lootkiste zu platzieren.")))
                .displayName(displayName)
                .dataContainer(CHEST_PLACER_ITEM_KEY, PersistentDataType.STRING, name)
                .build();
    }

    public void setLockDuration(Duration lockDuration) {
        this.lockDuration = lockDuration;
        callChangeObserver();
    }

    public void setSlotSize(int slotSize) {
        this.slotSize = slotSize;
        callChangeObserver();
    }

    public ParticleSpawner particleSpawner() {
        return particleSpawner;
    }

    public void setParticleSpawner(ParticleSpawner particleSpawner) {
        this.particleSpawner = particleSpawner;
        callChangeObserver();
    }

    public Material guiIconMaterial() {
        return guiIconMaterial;
    }

    public void setGuiIconMaterial(Material guiIconMaterial) {
        this.guiIconMaterial = guiIconMaterial;
        callChangeObserver();
    }

    public void callChangeObserver() {
        if (observer != null) {
            observer.onChange(this);
        }
    }

    public LootChestChangeObserver getObserver() {
        return observer;
    }

    public void setObserver(LootChestChangeObserver observer) {
        this.observer = observer;
    }
}
