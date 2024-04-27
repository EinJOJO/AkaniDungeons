package it.einjojo.akani.dungeon.mobs;

import it.einjojo.akani.dungeon.AkaniDungeon;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class AsyncMobPopulateChunkSelector implements Runnable {
    private final AkaniDungeon akaniDungeon;
    private BukkitTask task;

    public AsyncMobPopulateChunkSelector(AkaniDungeon akaniDungeon) {
        this.akaniDungeon = akaniDungeon;
    }

    @Override
    public void run() {

    }


    public void start(JavaPlugin plugin, int interval) {
        if (task != null) {
            stop();
        }
        task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this, 0, interval);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }
}
