package it.einjojo.akani.dungeon.lootchest;

import it.einjojo.akani.dungeon.util.RepeatingTask;
import org.bukkit.scheduler.BukkitTask;

import java.util.LinkedList;
import java.util.List;

public class LootChestTickTask implements RepeatingTask {
    private final List<PlacedLootChest> tickingChests = new LinkedList<>();
    private BukkitTask task;

    public boolean add(PlacedLootChest chest) {
        return tickingChests.add(chest);
    }

    public boolean remove(PlacedLootChest chest) {
        return tickingChests.remove(chest);
    }

    public List<PlacedLootChest> renderingChests() {
        return tickingChests;
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
        for (PlacedLootChest chest : tickingChests) {
            chest.tick();
        }
    }
}
