package it.einjojo.akani.dungeon.mine;

import it.einjojo.akani.dungeon.util.ChunkPosition;
import it.einjojo.akani.dungeon.util.ChunkRing;
import it.einjojo.akani.dungeon.util.RepeatingTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class SyncOreRenderer implements RepeatingTask {
    private static final ChunkRing RING = new ChunkRing(null,  //  9x9 pattern
            "2 2 2 2 2 2 2 2 2",
            "2 2 2 2 2 2 2 2 2",
            "2 2 1 1 1 1 1 2 2",
            "2 2 1 1 1 1 1 2 2",
            "2 2 1 1 0 1 1 2 2",
            "2 2 1 1 1 1 1 2 2",
            "2 2 1 1 1 1 1 2 2",
            "2 2 2 2 2 2 2 2 2",
            "2 2 2 2 2 2 2 2 2");
    private final MineManager mineManager;
    private final Map<UUID, Set<MineChunk>> rendered_chunks = new HashMap<>();
    private final Map<UUID, ChunkPosition> lastChunk = new HashMap<>();
    private BukkitTask task;

    public SyncOreRenderer(MineManager mineManager) {
        this.mineManager = mineManager;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ChunkPosition currentChunk = ChunkPosition.of(player.getLocation());
            ChunkPosition last = lastChunk.get(player.getUniqueId());
            if (last != null && last.equals(currentChunk)) {
                continue;
            }
            RING.setZero(currentChunk);
            lastChunk.put(player.getUniqueId(), currentChunk);
            for (Map.Entry<Character, List<ChunkPosition>> entry : RING.chunks().entrySet()) {
                Set<MineChunk> newRendered = new HashSet<>();
                for (ChunkPosition chunkPosition : entry.getValue()) {
                    // render everything
                    MineChunk mineChunk = mineManager.chunkByPosition(chunkPosition);
                    if (mineChunk == null) {
                        continue;
                    }
                    mineChunk.renderOres(player);
                    newRendered.add(mineChunk);
                }
                player.sendActionBar("Rendered " + newRendered.size() + " chunks");
                Set<MineChunk> oldRendered = rendered_chunks.get(player.getUniqueId());
                if (oldRendered != null) {
                    oldRendered.removeAll(newRendered);
                    for (MineChunk mineChunk : oldRendered) {
                        mineChunk.unrenderOres(player);
                        player.sendMessage("Unrendering chunk: " + mineChunk.position());
                    }
                }
                rendered_chunks.put(player.getUniqueId(), newRendered);

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
