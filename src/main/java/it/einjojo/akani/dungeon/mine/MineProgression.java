package it.einjojo.akani.dungeon.mine;

import org.jetbrains.annotations.Nullable;

/**
 * Represents the progression of a player's mining.
 */
public class MineProgression {
    private static final int ORE_TIMEOUT = 2 * 1000;
    private MineOre lastOre;
    private long lastOreTime;
    private int stage;

    public @Nullable MineOre lastOre() {
        return lastOre;
    }

    /**
     * Progresses the player's mine progression.
     *
     * @param ore the ore that the player is currently mining
     */
    public void progress(MineOre ore) {
        if (lastOre != null && lastOre != ore) {
            reset();
        }
        lastOre = ore;
        lastOreTime = System.currentTimeMillis();
        stage++;
    }

    void reset() {
        lastOre = null;
        lastOreTime = 0;
        stage = 0;
    }

    public int stage() {
        return stage;
    }

    public boolean isMining() {
        return lastOre != null && !hasExpired();
    }

    public boolean hasExpired() {
        return System.currentTimeMillis() - lastOreTime >= ORE_TIMEOUT;
    }
}
