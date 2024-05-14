package it.einjojo.akani.dungeon.lootchest.handler;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockAction;
import it.einjojo.akani.dungeon.lootchest.DefaultPlacedLootChest;
import it.einjojo.akani.dungeon.util.ClientArmorStandUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultPlacedChestHandler implements IDefaultPlacedChestHandler {
    private static final AtomicInteger HOLOGRAM_ENTITY_ID = new AtomicInteger(255_650_000);
    private static final BlockData CHEST_BLOCKDATA = Material.CHEST.createBlockData();
    private static final int CHEST_BLOCK_ID = 54;
    private static final BlockData BARRIER_BLOCKDATA = Material.BARRIER.createBlockData();
    private static final SecureRandom RANDOM = new SecureRandom();
    private Map<DefaultPlacedLootChest, Integer> hologramEntityIds = new HashMap<>();


    @Override
    public void onChestOpen(DefaultPlacedLootChest chest, Player player) {
        Inventory inv = chest.getInventory();
        int slot = 0;
        List<ItemStack> loot = chest.lootChest().generateRandomLoot();
        int maxSkip = inv.getSize() / loot.size(); // e.g. 27 / 4 items = 6
        for (ItemStack itemStack : loot) {
            inv.setItem(slot, itemStack);
            slot = (slot + RANDOM.nextInt(maxSkip)) % inv.getSize(); // overwrite if unlucky :)
        }
        player.openInventory(chest.getInventory());
        sendBlockAction(chest, player, true);
    }

    @Override
    public void onChestClose(DefaultPlacedLootChest chest, Player player) {

    }

    @Override
    public boolean preLock(DefaultPlacedLootChest chest, UUID player, Duration duration) {
        return true;
    }

    @Override
    public void postUnlock(DefaultPlacedLootChest chest, UUID player) {
        Player bukkitPlayer = Bukkit.getPlayer(player);
        if (bukkitPlayer == null) return;
        if (chest.viewers().contains(player)) {
            sendBlockAction(chest, bukkitPlayer, false);
        }
    }

    public void sendBlockAction(DefaultPlacedLootChest plc, Player player, boolean open) {
        Location loc = plc.location();
        var packet = new WrapperPlayServerBlockAction(
                new Vector3i(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()),
                1,
                open ? 1 : 0,
                CHEST_BLOCK_ID
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
    }


    @Override
    public void spawnChest(DefaultPlacedLootChest chest, Player player) {
        player.sendBlockChange(chest.location(), CHEST_BLOCKDATA);
        int id = hologramEntityIds.computeIfAbsent(chest, k -> HOLOGRAM_ENTITY_ID.getAndIncrement());
        ClientArmorStandUtil.spawn(player, id, chest.location());
        ClientArmorStandUtil.setName(player, id, chest.lootChest().displayName());
        if (!chest.canOpen(player.getUniqueId())) {
            sendBlockAction(chest, player, true);
        }
    }


    @Override
    public void despawnChest(DefaultPlacedLootChest chest, Player player) {
        player.sendBlockChange(chest.location(), BARRIER_BLOCKDATA);
        int id = hologramEntityIds.get(chest);
        ClientArmorStandUtil.despawn(player, id);
    }
}
