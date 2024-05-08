package it.einjojo.akani.dungeon.mine.tool;

/**
 * Represents a tool that can be used to mine blocks
 */
public interface Tool {
    /**
     * The damage that the tool can deal
     *
     * @return the damage
     */
    float damage();

    /**
     * Multiplier for the loot that the tool can drop
     *
     * @return the multiplier
     */
    float lootMultiplier();

    /**
     * Whether the tool can bypass the maximum loot that can be dropped from a mine ore.
     * Used for tools that have a high loot multiplier and should bypass the maximum loot.
     *
     * @return whether the tool can bypass the maximum loot
     */
    boolean isBypassMaximumLoot();

    /**
     * The type of the tool
     *
     * @return the type
     */
    ToolType type();

    record Impl(float damage, ToolType type, float lootMultiplier, boolean isBypassMaximumLoot) implements Tool {
    }
}
