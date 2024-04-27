package it.einjojo.akani.dungeon.mobs;

import it.einjojo.akani.dungeon.AkaniDungeon;
import it.einjojo.akani.dungeon.util.RepeatingTask;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayDeque;

public class SyncMobSpawner implements RepeatingTask {
    private static final int MAX_NANOS_PER_TICK = (int) (2 * 1E6); // 2ms
    private final ArrayDeque<Spawnable<?>> spawnQueue = new ArrayDeque<>();
    private final AkaniDungeon akaniDungeon;
    private BukkitTask task;

    public SyncMobSpawner(AkaniDungeon akaniDungeon) {
        this.akaniDungeon = akaniDungeon;
    }


    public void add(Spawnable<?> spawnable) {
        if (spawnable == null) return;
        if (spawnable.forceSpawnOnLag()) {
            spawnQueue.addFirst(spawnable);
        } else {
            spawnQueue.add(spawnable);
        }
    }

    @Override
    public void run() {
        long start = System.nanoTime();
        Spawnable<?> spawnable;
        while (!spawnQueue.isEmpty() && System.nanoTime() - start < MAX_NANOS_PER_TICK && (spawnable = spawnQueue.poll()) != null) {
            spawnable.spawn();
        }
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
