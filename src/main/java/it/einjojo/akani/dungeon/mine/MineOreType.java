package it.einjojo.akani.dungeon.mine;

import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import it.einjojo.akani.core.paper.util.ItemBuilder;
import it.einjojo.akani.dungeon.mine.tool.ToolType;
import it.einjojo.akani.dungeon.util.ItemReward;
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
    private final String name;
    private final ItemStack icon;
    private final List<ItemReward> itemRewards;
    private final com.github.retrooper.packetevents.protocol.item.ItemStack protocolItemStack;
    private float maxHealth;
    private ToolType toolType;
    private Hardness hardness;
    private Duration respawnTime;

    public MineOreType(String name, ItemStack icon, List<ItemReward> itemRewards, Hardness hardness, float maxHealth, ToolType toolType, Duration respawnTime) {
        this.itemRewards = itemRewards;
        this.hardness = hardness;
        this.icon = icon;
        protocolItemStack = SpigotConversionUtil.fromBukkitItemStack(icon);
        this.name = name;
        this.maxHealth = maxHealth;
        this.toolType = toolType;
        this.respawnTime = respawnTime;
    }

    public static @Nullable String spawnEggName(ItemStack itemStack) {
        if (itemStack == null || itemStack.getItemMeta() == null) return null;
        if (!itemStack.getType().equals(Material.ALLAY_SPAWN_EGG)) return null;
        return itemStack.getItemMeta().getPersistentDataContainer().get(SPAWN_EGG_KEY, PersistentDataType.STRING);
    }

    public List<Component> description() {
        return List.of(
                Component.text("§7▶ Name: §c" + name()),
                Component.text("§7▶ HP: §c" + maxHealth() + " ❤"),
                Component.text("§7▶ Härte: §c" + hardness().name()),
                Component.text("§7▶ Tool: §c" + toolType().name()),
                Component.text("§7▶ Respawnzeit: §c" + respawnTime().toMinutes() + " Minuten")
        );
    }



    public void setToolType(ToolType toolType) {
        this.toolType = toolType;
    }

    public ToolType toolType() {
        return toolType;
    }

    @Override
    public final boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof MineOreType oreType)) return false;

        return name.equals(oreType.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * @return Time it takes for the ore to respawn
     */
    public Duration respawnTime() {
        return respawnTime;
    }

    public float maxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(float maxHealth) {
        this.maxHealth = maxHealth;
    }

    /**
     * @return the ItemStack of the spawn egg for this ore type;
     */
    public ItemStack spawnEggItemStack() {
        return new ItemBuilder(Material.ALLAY_SPAWN_EGG)
                .displayName(Component.text("Spawn " + name()).color(NamedTextColor.GOLD))
                .dataContainer(SPAWN_EGG_KEY, PersistentDataType.STRING, name())
                .lore(List.of(
                        Component.text("Right click to spawn a " + name() + " ore.").color(NamedTextColor.GRAY)
                )).build();

    }

    public boolean canBreak(ItemStack tool) {
        if (!toolType().check(tool.getType())) return false;
        return Hardness.canBreak(tool.getType(), hardness);
    }

    public List<ItemStack> breakRewards(ItemStack toolUsed) {
        if (itemRewards == null || itemRewards.isEmpty()) return List.of();
        List<ItemStack> rewards = new ArrayList<>();
        for (ItemReward potentialReward : itemRewards) {
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

    public List<ItemReward> breakRewards() {
        return itemRewards;
    }

    public void setRespawnTime(Duration respawnTime) {
        this.respawnTime = respawnTime;
    }

    public Hardness hardness() {
        return hardness;
    }

    public void setHardness(Hardness hardness) {
        this.hardness = hardness;
    }

    public com.github.retrooper.packetevents.protocol.item.ItemStack protocolIcon() {
        return protocolItemStack;
    }
}
