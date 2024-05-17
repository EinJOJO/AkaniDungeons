package it.einjojo.akani.dungeon.lootchest.bukkit;

import it.einjojo.akani.dungeon.lootchest.LootChestManager;
import it.einjojo.akani.dungeon.util.RepeatingTask;
import org.bukkit.scheduler.BukkitTask;

public class AsyncLootChestSaveTask implements RepeatingTask {
    private final LootChestManager manager;
    private BukkitTask task;

    public AsyncLootChestSaveTask(LootChestManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public BukkitTask task() {
        return task;
    }

    @Override
    public void setTask(BukkitTask task) {
        this.task = task;
    }

    @Override
    public void run() {
        manager.save();
    }
}
