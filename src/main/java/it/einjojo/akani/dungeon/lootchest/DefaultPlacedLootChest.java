package it.einjojo.akani.dungeon.lootchest;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockAction;
import it.einjojo.akani.dungeon.util.PlayerTimestamp;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.*;

/**
 * Can be placed in the world and opened by players to get loot and gets locked for a certain duration after being opened.
 *
 * @param lootChest
 * @param viewers
 * @param lockedPlayers
 * @param canReopenTimes
 * @param location
 */
public record DefaultPlacedLootChest(LootChest lootChest, Set<UUID> viewers, Set<UUID> lockedPlayers,
                                     Queue<PlayerTimestamp> canReopenTimes,
                                     Location location) implements PlacedLootChest {
    private static final short CHEST_RENDER_DISTANCE_SQUARED = 4096;
    private static final short PARTICLE_RENDER_DISTANCE_SQUARED = 576;
    private static final BlockData CHEST_BLOCKDATA = Material.CHEST.createBlockData();
    private static final int CHEST_BLOCK_ID = 54;
    private static final BlockData UNRENDER_BLOCKDATA = Material.AIR.createBlockData();
    private static final SecureRandom RANDOM = new SecureRandom();


    public void tick(Collection<? extends Player> affectedPlayers) {
        checkUnlock();
        for (Player player : affectedPlayers) {
            if (player.getLocation().distanceSquared(location) < CHEST_RENDER_DISTANCE_SQUARED) {
                trySpawn(player);
                trySpawnParticles(player);
            } else {
                tryDespawn(player);
            }
        }
    }

    public void trySpawn(Player player) {
        if (viewers.add(player.getUniqueId())) {
            player.sendBlockChange(location, CHEST_BLOCKDATA);

            if (!canOpen(player.getUniqueId())) {
                sendBlockAction(player, true);
            }
        }
    }

    @Override
    public void tryDespawn(Player player) {
        if (viewers.remove(player.getUniqueId())) {
            player.sendBlockChange(location, UNRENDER_BLOCKDATA);
        }
    }

    public void trySpawnParticles(Player player) {
        if (player.getLocation().distanceSquared(location) < PARTICLE_RENDER_DISTANCE_SQUARED && canOpen(player.getUniqueId())) {
            lootChest().particleSpawner().spawnParticle(player, location);
        }
    }

    /**
     * Lock the chest for a player
     *
     * @param player the player to lock the chest for
     */
    public void lock(UUID player, Duration duration) {
        lockedPlayers.add(player);
        canReopenTimes.add(new PlayerTimestamp(player, System.currentTimeMillis() + duration.toMillis()));
    }

    public void unlock(UUID player) {
        lockedPlayers.remove(player);
        Player bukkitPlayer = Bukkit.getPlayer(player);
        if (bukkitPlayer != null && viewers.contains(player)) {
            sendBlockAction(bukkitPlayer, false);
        }
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

    public void sendBlockAction(Player player, boolean open) {
        Location loc = location();
        var packet = new WrapperPlayServerBlockAction(
                new Vector3i(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()),
                1,
                open ? 1 : 0,
                CHEST_BLOCK_ID
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
    }

    public boolean canOpen(UUID player) {
        return !lockedPlayers.contains(player);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

    /**
     * Opening the chest will open the inventory with the loot and lock the chest
     */
    public boolean open(Player player) {
        sendBlockAction(player, true);
        if (!canOpen(player.getUniqueId())) {
            return false;
        }
        Inventory inv = getInventory();
        List<ItemStack> loot = lootChest.generateRandomLoot();
        for (ItemStack itemStack : loot) {
            inv.setItem(RANDOM.nextInt(lootChest.slotSize()), itemStack);
        }
        player.openInventory(inv);
        player.playSound(player, Sound.BLOCK_ENDER_CHEST_OPEN, 1, 1.5f);
        lock(player.getUniqueId(), lootChest.lockDuration());
        return true;
    }


    @Override
    public @NotNull Inventory getInventory() {
        return Bukkit.createInventory(this, lootChest.slotSize(), lootChest.displayName());
    }
}
