package it.einjojo.akani.dungeon.mine;

import it.einjojo.akani.dungeon.util.RepeatingTask;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class SyncOreRenderer implements RepeatingTask {
    private final MineManager mineManager;
    private BukkitTask task;

    public SyncOreRenderer(MineManager mineManager) {
        this.mineManager = mineManager;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            MineProgression progression = mineManager.progressionByPlayer(player.getUniqueId());
            if (progression == null || !progression.isMining()) {
                continue;
            }

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
