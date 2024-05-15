package it.einjojo.akani.dungeon.listener;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import it.einjojo.akani.dungeon.lootchest.DefaultPlacedLootChest;
import it.einjojo.akani.dungeon.lootchest.PlacedLootChest;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class LootChestListener implements Listener, PacketListener {
    private final JavaPlugin plugin;
    private final Map<Location, PlacedLootChest> placedLootChestMap = new HashMap<>();

    public LootChestListener(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        PacketEvents.getAPI().getEventManager().registerListener(this, PacketListenerPriority.NORMAL);
    }

    @EventHandler
    public void removeFromViewers(PlayerQuitEvent event) {
        for (PlacedLootChest lootChest : placedLootChestMap.values()) {
            lootChest.unrender(event.getPlayer());
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
    public void placeLootChest(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }

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
        if (plc.open(player)) {}
    }


    @EventHandler
    public void triggerPlacedChestOnCloseHandler(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof DefaultPlacedLootChest plc) {
            plc.onClose(event);
        }
    }

}
