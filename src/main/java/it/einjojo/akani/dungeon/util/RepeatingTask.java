package it.einjojo.akani.dungeon.util;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public interface RepeatingTask extends Runnable {

    default boolean isAsync() {
        return false;
    }

    default void start(JavaPlugin plugin, int interval) {
        if (task() != null) {
            stop();
        }
        if (isAsync()) {
            setTask(plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this, 0, interval));
        } else {
            setTask(plugin.getServer().getScheduler().runTaskTimer(plugin, this, 0, interval));
        }
    }

    BukkitTask task();

    void setTask(BukkitTask task);

    default void stop() {
        if (task() != null) {
            task().cancel();
        }
    }

}
