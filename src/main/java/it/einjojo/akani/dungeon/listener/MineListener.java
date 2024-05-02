package it.einjojo.akani.dungeon.listener;

import it.einjojo.akani.dungeon.AkaniDungeon;
import it.einjojo.akani.dungeon.mine.MineManager;
import it.einjojo.akani.dungeon.mine.MineOre;
import it.einjojo.akani.dungeon.mine.MineOreType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class MineListener implements Listener {
    private final AkaniDungeon akaniDungeon;

    public MineListener(JavaPlugin plugin, AkaniDungeon akaniDungeon) {
        this.akaniDungeon = akaniDungeon;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    MineManager mineManager() {
        return akaniDungeon.mineManager();
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        mineManager().createProgression(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        mineManager().removeProgression(event.getPlayer().getUniqueId());
    }

    public void placeOre(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        ItemStack usedItem = event.getItem();
        if (usedItem == null || usedItem.getType().equals(Material.AIR)) {
            return;
        }
        Location location = event.getClickedBlock().getLocation();
        String spawnEggName = MineOreType.spawnEggName(usedItem);
        if (spawnEggName == null) return;
        MineOreType oreType = akaniDungeon.configManager().mineOreTypeConfig().types().stream().filter(type -> type.name().equals(spawnEggName)).findFirst().orElse(null);
        if (oreType == null) {
            event.getPlayer().sendMessage("§cOre type not found. Maybe deleted?");
        }
        ;

        MineOre ore = akaniDungeon.mineOreFactory().createMineOre(location, oreType);
        mineManager().registerOre(ore);
        ore.render(event.getPlayer());


    }

}
