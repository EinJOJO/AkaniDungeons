package it.einjojo.akani.dungeon.lootchest;

import it.einjojo.akani.dungeon.util.PlayerTimestamp;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

public record PlacedLootChest(LootChest lootChest, Set<UUID> viewers, Set<UUID> lockedPlayers,
                              Queue<PlayerTimestamp> canReopenTimes, Location location,
                              PlacedChestObserver observer) implements InventoryHolder {
    private static final BlockData CHEST_BLOCKDATA = Material.CHEST.createBlockData();
    private static final BlockData BARRIER_BLOCKDATA = Material.BARRIER.createBlockData();
    private static final short CHEST_RENDER_DISTANCE_SQUARED = 4096;
    private static final short PARTICLE_RENDER_DISTANCE_SQUARED = 576;

    /**
     * Called when the chest is in render distance
     *
     * @param player the player to render the chest for
     */
    public void renderTick(Player player) {
        if (player.getLocation().distanceSquared(location) < CHEST_RENDER_DISTANCE_SQUARED) {
            spawnChest(player);
            spawnParticles(player);
        } else {
            despawnChest(player);
        }
    }

    private void spawnChest(Player player) {
        if (viewers.add(player.getUniqueId())) {
            player.sendBlockChange(location, CHEST_BLOCKDATA);
        }
    }

    private void despawnChest(Player player) {
        if (viewers.remove(player.getUniqueId())) {
            player.sendBlockChange(location, BARRIER_BLOCKDATA);
        }
    }


    private void spawnParticles(Player player) {

    }

    /**
     * Lock the chest for a player
     *
     * @param player the player to lock the chest for
     */
    public void lock(UUID player, Duration duration) {
        lockedPlayers.add(player);
        canReopenTimes.add(new PlayerTimestamp(player, System.currentTimeMillis() + duration.toMillis()));
        observer.onLock(this, player, duration);

    }

    /**
     * Updates the times of the chest and unlocks players if necessary and notifies the observer
     */
    public void updateTimes() {
        PlayerTimestamp action = canReopenTimes.peek();
        while (action != null && action.timestamp() < System.currentTimeMillis()) {
            canReopenTimes.poll();
            lockedPlayers.remove(action.player());
            observer.onUnlock(this, action.player());
            action = canReopenTimes.peek();
        }
    }

    public boolean canOpen(UUID player) {
        return lockedPlayers.contains(player);
    }

    public void open(Player player) {
        observer.onChestOpen(this, player);
    }


    @Override
    public @NotNull Inventory getInventory() {
        return Bukkit.createInventory(this, 27, lootChest.displayName());
    }
}
