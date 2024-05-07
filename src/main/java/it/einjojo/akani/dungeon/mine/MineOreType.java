package it.einjojo.akani.dungeon.mine;

import it.einjojo.akani.core.paper.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


public class MineOreType {
    private static final NamespacedKey SPAWN_EGG_KEY = new NamespacedKey("akani", "mine_ore_spawn_egg");
    private static final Duration RESPAWN_TIME = Duration.ofMinutes(30);
    private final String name;
    private final ItemStack icon;
    private final List<BreakReward> breakRewards;
    private final float maxHealth;
    private Hardness hardness;

    public MineOreType(String name, ItemStack icon, List<BreakReward> breakRewards, Hardness hardness, float maxHealth) {
        this.breakRewards = breakRewards;
        this.hardness = hardness;
        this.icon = icon;
        this.name = name;
        this.maxHealth = maxHealth;
    }

    public static @Nullable String spawnEggName(ItemStack itemStack) {
        if (itemStack == null || itemStack.getItemMeta() == null) return null;
        if (!itemStack.getType().equals(Material.ALLAY_SPAWN_EGG)) return null;
        return itemStack.getItemMeta().getPersistentDataContainer().get(SPAWN_EGG_KEY, PersistentDataType.STRING);
    }

    public Duration respawnTime() {
        return RESPAWN_TIME;
    }

    public float maxHealth() {
        return maxHealth;
    }

    /**
     * @return the ItemStack of the spawn egg for this ore type;
     */
    public ItemStack spawnEggItemStack() {
        return new ItemBuilder(Material.ALLAY_SPAWN_EGG)
                .displayName(Component.text("Spawn " + name).color(NamedTextColor.GOLD))
                .dataContainer(SPAWN_EGG_KEY, PersistentDataType.STRING, name)
                .lore(List.of(
                        Component.text("Right click to spawn a " + name + " ore.").color(NamedTextColor.GRAY)
                )).build();

    }

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

    public String name() {
        return name;
    }

    public ItemStack icon() {
        return icon.clone();
    }

    public List<BreakReward> breakRewards() {
        return breakRewards;
    }

    public Hardness hardness() {
        return hardness;
    }

    public void setHardness(Hardness hardness) {
        this.hardness = hardness;
    }
}
