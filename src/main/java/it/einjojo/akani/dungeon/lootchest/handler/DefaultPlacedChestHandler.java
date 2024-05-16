package it.einjojo.akani.dungeon.lootchest.handler;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockAction;
import it.einjojo.akani.dungeon.lootchest.DefaultPlacedLootChest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

public class DefaultPlacedChestHandler implements IDefaultPlacedChestHandler {
    private static final BlockData CHEST_BLOCKDATA = Material.CHEST.createBlockData();
    private static final int CHEST_BLOCK_ID = 54;
    private static final BlockData UNRENDER_BLOCKDATA = Material.AIR.createBlockData();
    private static final SecureRandom RANDOM = new SecureRandom();


    @Override
    public void onChestOpen(DefaultPlacedLootChest chest, Player player) {
        Inventory inv = chest.getInventory();
        int slot = 0;
        List<ItemStack> loot = chest.lootChest().generateRandomLoot();
        player.sendMessage("!" + loot.size());
        for (ItemStack itemStack : loot) {
            inv.setItem(RANDOM.nextInt(chest.lootChest().slotSize()), itemStack);
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

        if (!chest.canOpen(player.getUniqueId())) {
            sendBlockAction(chest, player, true);
        }
    }


    @Override
    public void despawnChest(DefaultPlacedLootChest chest, Player player) {
        player.sendBlockChange(chest.location(), UNRENDER_BLOCKDATA);

    }
}
