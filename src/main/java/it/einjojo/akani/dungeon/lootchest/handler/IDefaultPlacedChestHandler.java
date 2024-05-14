package it.einjojo.akani.dungeon.lootchest.handler;

import it.einjojo.akani.dungeon.lootchest.DefaultPlacedLootChest;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.UUID;

public interface IDefaultPlacedChestHandler {

    /**
     * Called when a chest is opened
     *
     * @param chest  the chest that was opened
     * @param player the player that opened the chest
     */
    void onChestOpen(DefaultPlacedLootChest chest, Player player);

    /**
     * Called when a chest is closed
     *
     * @param chest  the chest that was closed
     * @param player the player that closed the chest
     */
    void onChestClose(DefaultPlacedLootChest chest, Player player);

    /**
     * Called when a chest is locked
     *
     * @param chest    the chest that was locked
     * @param player   the player that locked the chest
     * @param duration the duration the chest was locked for
     */
    boolean preLock(DefaultPlacedLootChest chest, UUID player, Duration duration);

    /**
     * Called when a chest is unlocked
     *
     * @param chest  the chest that was unlocked
     * @param player the player that unlocked the chest
     */
    void postUnlock(DefaultPlacedLootChest chest, UUID player);

    void spawnChest(DefaultPlacedLootChest chest, Player player);
    void despawnChest(DefaultPlacedLootChest chest, Player player);


}
