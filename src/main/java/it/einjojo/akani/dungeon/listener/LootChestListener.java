package it.einjojo.akani.dungeon.listener;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import it.einjojo.akani.dungeon.lootchest.*;
import it.einjojo.akani.dungeon.util.BuilderRegistry;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;

public class LootChestListener implements Listener, PacketListener {
    private final JavaPlugin plugin;
    private final LootChestManager lootChestManager;
    private final PlacedLootChestFactory lootChestFactory = new PlacedLootChestFactory();


    public LootChestListener(JavaPlugin plugin, LootChestManager lootChestManager) {
        this.plugin = plugin;
        this.lootChestManager = lootChestManager;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        PacketEvents.getAPI().getEventManager().registerListener(this, PacketListenerPriority.NORMAL);
    }

    @EventHandler
    public void removeFromViewers(PlayerQuitEvent event) {
        for (PlacedLootChest lootChest : lootChestManager.placedLootChestMap().values()) {
            lootChest.tryDespawn(event.getPlayer());
        }
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
            LootChest resolved = lootChestManager.lootChestByName(name);
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
            lootChestManager.persistPlacedChest(lootChestFactory.createSimplePlacedLootChest(resolved, location));
            event.setCancelled(true);
        });
    }

    @EventHandler
    public void destroyLootChest(BlockBreakEvent event) {
        if (BuilderRegistry.isNotInBuildMode(event.getPlayer().getUniqueId())) return;
        PlacedLootChest plc = lootChestManager.placedLootChestByLocation(event.getBlock().getLocation());
        if (plc == null) {
            return;
        }
        lootChestManager.deletePlacedChest(plc);
        event.getPlayer().sendMessage("§cDie platzierte Lootchest wurde gelöscht.");
    }

    @EventHandler(priority = EventPriority.LOW)
    public void placeLootChestIfShulker(PlayerInteractEvent interactEvent) {
        if (interactEvent.getHand() != EquipmentSlot.HAND) return;
        if (interactEvent.getClickedBlock() == null) return;
        LootChest lootChest = null;
        Material material = interactEvent.getClickedBlock().getType();
        if (material.equals(Material.LIGHT_GRAY_SHULKER_BOX)) {
            lootChest = lootChestManager.lootChestByName("normal");
        } else if (material.equals(Material.PURPLE_SHULKER_BOX)) {
            lootChest = lootChestManager.lootChestByName("episch");
        } else if (material.equals(Material.GREEN_SHULKER_BOX)) {
            lootChest = lootChestManager.lootChestByName("selten");
        } else if (material.equals(Material.YELLOW_SHULKER_BOX)) {
            lootChest = lootChestManager.lootChestByName("Legendär");
        } else if (material.equals(Material.BLUE_SHULKER_BOX)) {
            lootChest = lootChestManager.lootChestByName("sehr selten");
        }
        if (lootChest == null) {
            return;
        }
        lootChestManager.persistPlacedChest(lootChestFactory.createSimplePlacedLootChest(lootChest, interactEvent.getClickedBlock().getLocation()));
        interactEvent.getPlayer().sendMessage("§aDu hast eine Lootchest registriert!");
    }


    @EventHandler
    public void openChestOnInteract(PlayerInteractEvent event) {
        if (!event.hasBlock()) {
            return;
        }
        if (event.getClickedBlock() == null) {
            return;
        }
        if (!event.getAction().isRightClick()) return;
        PlacedLootChest plc = lootChestManager.placedLootChestByLocation(event.getClickedBlock().getLocation());
        if (plc == null) {
            return;
        }
        if (!(event.getHand() == EquipmentSlot.HAND)) return;
        Player player = event.getPlayer();
        event.setCancelled(true);
        plc.open(player);
    }


    @EventHandler
    public void triggerPlacedChestOnCloseHandler(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof DefaultPlacedLootChest plc) {
            plc.onClose(event);
        }
    }

}
