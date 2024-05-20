package it.einjojo.akani.dungeon.mobs;

import it.einjojo.akani.dungeon.AkaniDungeon;
import it.einjojo.akani.dungeon.mobs.spawnable.Spawnable;
import it.einjojo.akani.dungeon.util.RepeatingTask;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public class SyncMobSpawner implements RepeatingTask {
    private static final int MAX_NANOS_PER_TICK = (int) (2 * 1E6); // 2ms
    private final ArrayDeque<Spawnable<?>> spawnQueue = new ArrayDeque<>();
    Set<Entity> glowing = new HashSet<>();
    int glowingRemoval = 0;
    private BukkitTask task;


    public SyncMobSpawner() {
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
        removeGlowing();
        long start = System.nanoTime();
        Spawnable<?> spawnable;
        while (!spawnQueue.isEmpty() && System.nanoTime() - start < MAX_NANOS_PER_TICK && (spawnable = spawnQueue.poll()) != null) {
            spawnable.spawn();
        }
    }

    public void removeGlowing() {
        if (glowingRemoval != 10) {
            glowingRemoval++;
            return;
        }
        glowingRemoval = 0;
        for (Entity entity : glowing) {
            entity.setGlowing(false);
        }
        glowing.clear();
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
