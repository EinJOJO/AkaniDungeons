package it.einjojo.akani.dungeon.lootchest;

import it.einjojo.akani.dungeon.lootchest.handler.IDefaultPlacedChestHandler;
import it.einjojo.akani.dungeon.util.PlayerTimestamp;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Collection;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

/**
 * Can be placed in the world and opened by players to get loot and gets locked for a certain duration after being opened.
 *
 * @param lootChest
 * @param viewers
 * @param lockedPlayers
 * @param canReopenTimes
 * @param location
 * @param handler
 */
public record DefaultPlacedLootChest(LootChest lootChest, Set<UUID> viewers, Set<UUID> lockedPlayers,
                                     Queue<PlayerTimestamp> canReopenTimes, Location location,
                                     IDefaultPlacedChestHandler handler) implements PlacedLootChest {
    private static final short CHEST_RENDER_DISTANCE_SQUARED = 4096;
    private static final short PARTICLE_RENDER_DISTANCE_SQUARED = 576;


    public void tick(Collection<? extends Player> affectedPlayers) {
        checkUnlock();
        for (Player player : affectedPlayers) {
            if (player.getLocation().distanceSquared(location) < CHEST_RENDER_DISTANCE_SQUARED) {
                if (viewers.add(player.getUniqueId())) {
                    handler.spawnChest(this, player);
                }
                spawnParticles(player);
            } else {
                unrender(player);
            }
        }
    }

    @Override
    public void unrender(Player player) {
        if (viewers.remove(player.getUniqueId())) {
            handler.despawnChest(this, player);
        }
    }

    private void spawnParticles(Player player) {
        if (player.getLocation().distanceSquared(location) < PARTICLE_RENDER_DISTANCE_SQUARED && canOpen(player.getUniqueId())) {

        }
    }


    /**
     * Lock the chest for a player
     *
     * @param player the player to lock the chest for
     */
    public void lock(UUID player, Duration duration) {
        if (handler.preLock(this, player, duration)) {
            lockedPlayers.add(player);
            canReopenTimes.add(new PlayerTimestamp(player, System.currentTimeMillis() + duration.toMillis()));
        }
    }

    public void unlock(UUID player) {
        lockedPlayers.remove(player);
        handler.postUnlock(this, player);
    }

    /**
     * Unlocks players that can reopen the chest considering the current time
     */
    private void checkUnlock() {
        long currentMillis = System.currentTimeMillis();
        PlayerTimestamp action = canReopenTimes.peek();
        while (action != null && action.timestamp() < currentMillis) {
            unlock(canReopenTimes.poll().player());
            action = canReopenTimes.peek();
        }
    }

    public boolean canOpen(UUID player) {
        return !lockedPlayers.contains(player);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        handler().onChestClose(this, (Player) event.getPlayer());
    }

    /**
     * Opening the chest will open the inventory with the loot and lock the chest
     */
    public boolean open(Player player) {
        if (!canOpen(player.getUniqueId())) {
            return false;
        }
        handler.onChestOpen(this, player);
        lock(player.getUniqueId(), lootChest().lockDuration());
        return true;
    }


    @Override
    public @NotNull Inventory getInventory() {
        return Bukkit.createInventory(this, lootChest.slotSize(), lootChest.displayName());
    }
}
