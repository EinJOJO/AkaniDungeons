package it.einjojo.akani.dungeon.listener;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import it.einjojo.akani.dungeon.lootchest.*;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class LootChestListener implements Listener, PacketListener {
    private final JavaPlugin plugin;
    private final LootChestManager lootChestManager;
    private final Map<Location, PlacedLootChest> placedLootChestMap = new HashMap<>();


    public LootChestListener(JavaPlugin plugin, LootChestManager lootChestManager) {
        this.plugin = plugin;
        this.lootChestManager = lootChestManager;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        PacketEvents.getAPI().getEventManager().registerListener(this, PacketListenerPriority.NORMAL);
    }

    @EventHandler
    public void removeFromViewers(PlayerQuitEvent event) {
        for (PlacedLootChest lootChest : placedLootChestMap.values()) {
            lootChest.tryDespawn(event.getPlayer());
        }
    }

    public void registerPlacedLootChest(PlacedLootChest placedLootChest) {
        placedLootChestMap.put(placedLootChest.location(), placedLootChest);
    }

    public void unregisterPlacedLootChest(PlacedLootChest placedLootChest) {
        placedLootChestMap.remove(placedLootChest.location());
    }

    public PlacedLootChest placedLootChest(Location location) {
        return placedLootChestMap.get(location);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void placeLootChest(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        LootChest.chestTypeNameFromItemStack(event.getItem()).ifPresent(name -> {
            LootChest resolved = lootChestManager.chestByName(name);
            if (resolved == null) {
                return;
            }
            Location location = switch (event.getBlockFace()) {
                case UP -> clickedBlock.getLocation().add(0, 1, 0);
                case DOWN -> clickedBlock.getLocation().add(0, -1, 0);
                case NORTH -> clickedBlock.getLocation().add(0, 0, -1);
                case SOUTH -> clickedBlock.getLocation().add(0, 0, 1);
                case EAST -> clickedBlock.getLocation().add(1, 0, 0);
                case WEST -> clickedBlock.getLocation().add(-1, 0, 0);
                default -> clickedBlock.getLocation();
            };
            lootChestManager.persistPlacedChest(new PlacedLootChestFactory().createSimplePlacedLootChest(resolved, location));
        });
    }


    @EventHandler
    public void onInteractWithLootChestLocation(PlayerInteractEvent event) {
        if (!event.hasBlock()) {
            return;
        }
        PlacedLootChest plc = placedLootChest(event.getClickedBlock().getLocation());
        if (plc == null) {
            return;
        }
        if (!(event.getHand() == EquipmentSlot.HAND)) return;
        Player player = event.getPlayer();
        event.setCancelled(true);
        if (plc.open(player)) {
        }
    }


    @EventHandler
    public void triggerPlacedChestOnCloseHandler(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof DefaultPlacedLootChest plc) {
            plc.onClose(event);
        }
    }

}
