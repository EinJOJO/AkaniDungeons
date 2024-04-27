package it.einjojo.akani.dungeon.mine;

import it.einjojo.akani.dungeon.util.RepeatingTask;
import org.bukkit.scheduler.BukkitTask;

public class SyncOreRenderer implements RepeatingTask {
    private BukkitTask task;

    @Override
    public void run() {

    }

    @Override
    public BukkitTask task() {
        return task;
    }

    @Override
    public void setTask(BukkitTask task) {
        this.task = task;
    }
}
