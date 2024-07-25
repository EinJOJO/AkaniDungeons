package it.einjojo.akani.dungeon.listener;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerListener implements Listener {


    public PlayerListener(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void oNDeath(PlayerDeathEvent event) {
        event.getPlayer().spigot().respawn();
    }

    @EventHandler
    public void respawnRunCommand(PlayerPostRespawnEvent postRespawnEvent) {
        postRespawnEvent.getPlayer().chat("/warp dungeons");
    }


}
