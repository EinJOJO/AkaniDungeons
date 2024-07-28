package it.einjojo.akani.dungeon.listener;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import it.einjojo.akani.dungeon.util.BuilderRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumSet;
import java.util.UUID;

public class DungeonWorldListener implements Listener {

    private final JavaPlugin plugin;
    private static final EnumSet<Material> INTERACTION_BLACKLIST = EnumSet.of(
            Material.BUCKET,
            Material.WATER_BUCKET,
            Material.LAVA_BUCKET,
            Material.FLINT_AND_STEEL,
            Material.FIRE_CHARGE,
            Material.FIREWORK_ROCKET,
            Material.GLASS_BOTTLE,
            Material.FIREWORK_STAR
    );

    public DungeonWorldListener(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (!BuilderRegistry.isNotInBuildMode(event.getPlayer().getUniqueId())) {
            toggleBuild(event.getPlayer().getUniqueId());
        }
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

    @EventHandler
    public void keepInventory(PlayerDeathEvent event) {
        event.setKeepLevel(true);
        event.setDroppedExp(0);
        event.setKeepInventory(true);
        event.deathMessage(null);
        event.getDrops().clear();
    }


    @EventHandler
    public void respawnRunCommand(PlayerPostRespawnEvent postRespawnEvent) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            postRespawnEvent.getPlayer().chat("/warp dungeons");
        }, 1);
    }


    public void toggleBuild(UUID playerId) {
        BuilderRegistry.toggleBuild(playerId);
    }

    protected boolean isNotInBuildMode(Player player) {
        return BuilderRegistry.isNotInBuildMode(player.getUniqueId());
    }

}
