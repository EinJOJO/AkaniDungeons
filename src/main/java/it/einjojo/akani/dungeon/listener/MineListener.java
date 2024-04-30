package it.einjojo.akani.dungeon.listener;

import it.einjojo.akani.dungeon.mine.MineManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MineListener implements Listener {
    private final MineManager mineManager;

    public MineListener(MineManager mineManager, JavaPlugin plugin) {
        this.mineManager = mineManager;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        mineManager.createProgression(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        mineManager.removeProgression(event.getPlayer().getUniqueId());
    }

}
