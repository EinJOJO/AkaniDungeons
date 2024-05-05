package it.einjojo.akani.dungeon.mine;

import org.jetbrains.annotations.Nullable;

/**
 * Represents the progression of a player's mining.
 */
public class MineProgression {
    private static final int ORE_TIMEOUT = 3 * 1000; // 3 seconds timeout
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
        if (lastOre != ore) {
            reset();
        }
        if (!canProgressAgain()) return;
        lastOre = ore;
        lastOreTime = System.currentTimeMillis();
        stage++;
    }

    public boolean canProgressAgain() {
        return System.currentTimeMillis() - lastOreTime >= 700;
    }

    public long lastOreTime() {
        return lastOreTime;
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
