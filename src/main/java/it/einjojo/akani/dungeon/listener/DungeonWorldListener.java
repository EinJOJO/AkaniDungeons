package it.einjojo.akani.dungeon.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DungeonWorldListener implements Listener {

    private final Set<UUID> buildModeSet = new HashSet<>();

    public DungeonWorldListener(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        buildModeSet.remove(UUID.randomUUID());
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void cancelBlockBreak(BlockBreakEvent event) {
        if (isNotInBuildMode(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void cancelBlockPlace(BlockPlaceEvent event) {
        if (isNotInBuildMode(event.getPlayer())) {
            event.setBuild(false);
        }
    }


    public void toggleBuild(UUID playerId) {
        if (buildModeSet.contains(playerId)) {
            buildModeSet.remove(playerId);
        } else {
            buildModeSet.add(playerId);
        }
    }

    protected boolean isNotInBuildMode(Player player) {
        return !buildModeSet.contains(player.getUniqueId());
    }

}
