package it.einjojo.akani.dungeon.listener;

import it.einjojo.akani.dungeon.AkaniDungeon;
import it.einjojo.akani.dungeon.mine.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class MineListener implements Listener {
    private final AkaniDungeon akaniDungeon;

    public MineListener(JavaPlugin plugin, AkaniDungeon akaniDungeon) {
        this.akaniDungeon = akaniDungeon;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    MineManager mineManager() {
        return akaniDungeon.mineManager();
    }

    SyncOreRenderer oreRenderer() {
        return akaniDungeon.syncOreRenderer();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        mineManager().createProgression(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        mineManager().removeProgression(event.getPlayer().getUniqueId());
        // Unrender all ores for the player, so on rejoin they will be rendered again
        List<MineChunk> rendered = oreRenderer().renderedChunks().get(event.getPlayer().getUniqueId());
        if (rendered != null) {
            for (MineChunk mineChunk : rendered) {
                mineChunk.unrenderOres(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void placeOre(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        ItemStack usedItem = event.getItem();
        if (usedItem == null || usedItem.getType().equals(Material.AIR)) {
            return;
        }
        String spawnEggName = MineOreType.spawnEggName(usedItem);
        if (spawnEggName == null) {
            return;
        }
        Location location = event.getClickedBlock().getLocation();
        event.setCancelled(true);
        MineOreType oreType = akaniDungeon.configManager().mineOreTypeConfig().types().stream().filter(type -> type.name().equals(spawnEggName)).findFirst().orElse(null);
        if (oreType == null) {
            event.getPlayer().sendMessage("§cErz nicht gefunden. Wurde es gelöscht?");
            return;
        }
        PlacedOre ore = akaniDungeon.placedOreFactory().createPlacedOre(location.subtract(0, 0.3f, 0).setDirection(event.getPlayer().getLocation().getDirection()), oreType);
        mineManager().storage().createPlacedOre(ore);
        mineManager().registerPlacedOre(ore);
        event.getPlayer().sendMessage("§aErz platziert.");
        ore.render(event.getPlayer());
    }

}
