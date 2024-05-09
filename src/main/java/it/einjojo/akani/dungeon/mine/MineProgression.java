package it.einjojo.akani.dungeon.mine;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the progression of a player's mining.
 */
public class MineProgression {
    private static final int PROGRESS_BAR_LENGTH = 20;
    private static final int DELAY_MILLIS = 800;
    private static final Component COMPLETED_PROGRESS = Component.text("✔").color(NamedTextColor.GREEN);
    private static final int ORE_TIMEOUT = 3 * 1000; // 3 seconds timeout
    private PlacedOre lastOre;
    private long lastOreTimeMillis;
    private float damage;

    public @Nullable PlacedOre lastOre() {
        return lastOre;
    }

    /**
     * Progresses the player's mine progression.
     *
     * @param ore the ore that the player is currently mining
     * @return true if the progression was successful, false otherwise
     */
    public boolean progress(Player player, PlacedOre ore, float newDamage) {
        if (lastOre != ore || hasExpired()) {
            reset(player);
        } else if (!canProgressAgain()) {
            return false;
        } else if (isComplete()) {
            return false;
        }
        lastOre = ore;
        lastOreTimeMillis = System.currentTimeMillis();
        damage += newDamage;
        ore.setName(player, progressText());
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        return true;
    }

    public Component progressText() {
        if (!isMining()) {
            return Component.empty();
        }
        float relativeDamage = damage / lastOre.type().maxHealth(); // 0.0 -> >=1.0
        if (relativeDamage >= 1) {
            return COMPLETED_PROGRESS;
        }
        int greenLength = (int) (PROGRESS_BAR_LENGTH * relativeDamage);
        int grayLength = PROGRESS_BAR_LENGTH - greenLength;
        String green = "|".repeat(Math.max(0, greenLength));
        String gray = "|".repeat(Math.max(0, grayLength));
        return Component.text(green).color(NamedTextColor.GREEN).append(Component.text(gray).color(NamedTextColor.GRAY));
    }

    public boolean isComplete() {
        if (lastOre == null) {
            return false;
        }
        return damage >= lastOre.type().maxHealth();
    }

    public boolean canProgressAgain() {
        return System.currentTimeMillis() - lastOreTimeMillis >= DELAY_MILLIS;
    }

    public long lastOreTimeMillis() {
        return lastOreTimeMillis;
    }

    void reset(Player player) {
        if (lastOre != null) {
            lastOre.setName(player, null);
        }
        lastOre = null;
        lastOreTimeMillis = 0;
        damage = 0;
    }

    @Deprecated
    public int stage() {
        return (int) damage;
    }

    public float damage() {
        return damage;
    }

    public boolean isMining() {
        return lastOre != null && !hasExpired();
    }

    public boolean hasExpired() {
        return System.currentTimeMillis() - lastOreTimeMillis >= ORE_TIMEOUT;
    }
}
